#!/usr/bin/env python3
"""Summarises a TestNG run for the GitHub job summary and the annotations, and lists the tests to re-run.

    scripts/test-summary.py [results.xml ...]

Reads testng-results.xml (default: target/surefire-reports/ and test-output/), writes a markdown summary to
$GITHUB_STEP_SUMMARY (or stdout when it is not set) and prints one ::error annotation per failed test to stdout.
It never fails the step: whether the run is green or red is decided by "mvn test", not by this report.

Per <test> block of the suite (the suite-wise view): passed / failed / skipped, how many passed only on the
retry (flaky), and the time. Below it: every failed test with its first error line, then the flaky ones.

It also writes failed-tests.txt next to the results file: one "package.Class#method" per test that did not pass.
The next attempt of a failed CI job passes that file to "mvn test -DrerunTests=<file>" (listeners.RerunFailedTestsFilter)
so only those tests run again. No file is written when everything passed, so a later re-run is a full run.

A skipped test is listed only when its class had a failed setup method (@BeforeClass, @BeforeMethod): that skip is a
consequence of the failure. A skip a test chose itself (SkipException: missing test data) would only skip again.
"""
import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

DEFAULT_RESULTS = ["target/surefire-reports/testng-results.xml", "test-output/testng-results.xml"]
RERUN_FILE = "failed-tests.txt"
MAX_ANNOTATIONS = 10  # GitHub shows at most 10 error annotations per step
MESSAGE_LIMIT = 220


def find_results(args):
    paths = [Path(a) for a in args] or [Path(p) for p in DEFAULT_RESULTS]
    existing = [p for p in paths if p.is_file()]
    # The same run can leave a copy in both folders; one is enough.
    return existing[:1] if not args else existing


def params_of(method):
    return "|".join((value.text or "").strip() for value in method.iter("value"))


def first_line(text):
    for line in (text or "").splitlines():
        if line.strip():
            return line.strip()
    return ""


def clip(text, limit=MESSAGE_LIMIT):
    return text if len(text) <= limit else text[: limit - 1] + "…"


def cell(text):
    return text.replace("|", "\\|").replace("\n", " ")


def escape_annotation(text):
    return text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")


def human(ms):
    seconds = int(ms) // 1000
    hours, rest = divmod(seconds, 3600)
    minutes, seconds = divmod(rest, 60)
    if hours:
        return f"{hours}h {minutes}m"
    if minutes:
        return f"{minutes}m {seconds}s"
    return f"{seconds}s"


def read_blocks(path):
    """One entry per <test> block: {name, ms, tests, config_failed}.

    tests maps (class, method, params, n) to its attempts; an attempt is (status, message, exception class).
    config_failed is the set of classes with a failed setup/teardown method."""
    blocks = []
    root = ET.parse(path).getroot()
    for test in root.iter("test"):
        tests = {}
        last_invocation = {}
        config_failed = set()
        for cls in test.iter("class"):
            class_name = cls.get("name", "?")
            for method in cls.iter("test-method"):
                status = method.get("status", "")
                config = method.get("is-config") == "true"
                if config and status != "FAIL":
                    continue  # setup/teardown that worked (or was skipped) says nothing
                if config:
                    config_failed.add(class_name)
                method_name = ("[config] " if config else "") + method.get("name", "?")
                key = (class_name, method_name, params_of(method))
                exception = method.find("./exception")
                message = method.find("./exception/message")
                text = first_line(message.text if message is not None else "")
                exception_class = exception.get("class", "") if exception is not None else ""
                attempt = (status, text or exception_class, exception_class)
                previous = last_invocation.get(key)
                if previous is not None and tests[previous][-1][0] != "PASS":
                    tests[previous].append(attempt)  # the earlier attempt did not pass: this is its retry
                else:
                    last_invocation[key] = key + (len(tests),)
                    tests[key + (len(tests),)] = [attempt]
        blocks.append({"name": test.get("name", "?"), "ms": test.get("duration-ms", "0"), "tests": tests,
                       "config_failed": config_failed})
    return blocks


def classify(attempts):
    final = attempts[-1][0]
    if final == "PASS":
        return "flaky" if any(a[0] != "PASS" for a in attempts[:-1]) else "passed"
    if final == "FAIL":
        return "failed"
    return "skipped"


def write_rerun_list(results_path, tests):
    target = results_path.parent / RERUN_FILE
    if tests:
        target.write_text("\n".join(sorted(tests)) + "\n", encoding="utf-8")
    elif target.exists():
        target.unlink()  # a stale list must not outlive a green run


def main():
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    paths = find_results(sys.argv[1:])
    out = open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") if os.environ.get("GITHUB_STEP_SUMMARY") \
        else sys.stdout
    if not paths:
        out.write("### Test results\n\nNo TestNG results were found: the run stopped before the tests started.\n")
        return 0

    blocks, readable = [], []
    for path in paths:
        try:
            blocks.extend(read_blocks(path))
            readable.append(path)
        except ET.ParseError as error:
            # A run that was killed mid-way can leave a cut-off file; report it, keep the others.
            print(f"::warning::Could not read {path}: {error}")
    if not readable:
        out.write("### Test results\n\nThe TestNG results file could not be read (the run probably stopped while "
                  "writing it), so there is no per-suite summary and no list of tests to re-run.\n")
        return 0

    totals = {"passed": 0, "flaky": 0, "failed": 0, "skipped": 0}
    rows, failed, flaky, to_rerun = [], [], [], set()
    for block in blocks:
        counts = {"passed": 0, "flaky": 0, "failed": 0, "skipped": 0}
        for (cls, method, _params, _n), attempts in block["tests"].items():
            kind = classify(attempts)
            counts[kind] += 1
            name = f"{cls}.{method}"
            if kind == "failed":
                failed.append((block["name"], name, attempts[-1][1], len(attempts) > 1))
            elif kind == "flaky":
                first_failure = next(a for a in attempts if a[0] != "PASS")
                flaky.append((block["name"], name, first_failure[1]))
            is_test = not method.startswith("[config] ")
            own_skip = kind == "skipped" and attempts[-1][2] == "org.testng.SkipException" \
                and cls not in block["config_failed"]
            if is_test and (kind == "failed" or (kind == "skipped" and not own_skip)):
                to_rerun.add(f"{cls}#{method}")
        for kind, value in counts.items():
            totals[kind] += value
        if not block["tests"]:
            continue
        icon = "❌" if counts["failed"] else ("⚠️" if counts["skipped"] or counts["flaky"] else "✅")
        rows.append(f"| {icon} | {cell(block['name'])} | {counts['passed'] + counts['flaky']} | {counts['failed']} | "
                    f"{counts['skipped']} | {counts['flaky']} | {human(block['ms'])} |")

    total = sum(totals.values())
    icon = "❌" if totals["failed"] else "✅"
    lines = [f"### {icon} Test results: {total} tests, {totals['passed'] + totals['flaky']} passed, "
             f"{totals['failed']} failed, {totals['skipped']} skipped, {totals['flaky']} passed only on retry", ""]
    lines += ["| | Suite | Passed | Failed | Skipped | Flaky | Time |", "|---|---|--:|--:|--:|--:|--:|"] + rows + [""]
    if failed:
        lines += ["#### Failed tests", "", "| Suite | Test | First error line | Retried |", "|---|---|---|:-:|"]
        lines += [f"| {cell(suite)} | `{cell(name)}` | {cell(clip(message)) or '-'} | {'yes' if retried else 'no'} |"
                  for suite, name, message, retried in failed]
        lines.append("")
    if flaky:
        lines += ["#### Passed only on retry (flaky)", "", "| Suite | Test | Error of the first attempt |", "|---|---|---|"]
        lines += [f"| {cell(suite)} | `{cell(name)}` | {cell(clip(message)) or '-'} |" for suite, name, message in flaky]
        lines.append("")
    if totals["skipped"]:
        lines += ["Skipped tests are usually a precondition the test could not meet (missing test data) or a "
                  "failed setup earlier in the class.", ""]
    if to_rerun:
        lines += [f"**{len(to_rerun)} test(s) to run again:** \"Re-run failed jobs\" on this run runs only these.", ""]
    out.write("\n".join(lines) + "\n")

    write_rerun_list(readable[0], to_rerun)

    for suite, name, message, _retried in failed[:MAX_ANNOTATIONS]:
        print(f"::error title={escape_annotation(suite)}::{escape_annotation(name + ': ' + clip(message))}")
    if len(failed) > MAX_ANNOTATIONS:
        print(f"::notice::{len(failed) - MAX_ANNOTATIONS} more failed tests are listed in the job summary")
    return 0


if __name__ == "__main__":
    sys.exit(main())
