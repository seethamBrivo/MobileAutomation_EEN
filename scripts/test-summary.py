#!/usr/bin/env python3
"""Summarises a TestNG run for the GitHub job summary and the annotations, and lists the tests to re-run.

    scripts/test-summary.py [results.xml ...]

Reads testng-results.xml (default: target/surefire-reports/ and test-output/), writes a markdown summary to
$GITHUB_STEP_SUMMARY (or stdout when it is not set) and prints ::error annotations to stdout.

The report never decides whether the run is green: "mvn test" does. The one exception is a re-run. When RERUN_LIST
names the file the run was started with (-DrerunTests), the step fails if a listed test did not show up in the
results (renamed, removed, or not in the chosen suite) or if no test ran at all: a re-run that verified nothing
must not look green.

Per <test> block of the suite (the suite-wise view): passed / failed / skipped, how many passed only on the
retry (flaky), and the time. Below it: failed setup methods, every failed test with its first error line, and the
flaky ones. Setup and teardown methods are not tests: they are listed apart and not counted in the totals.

A retried attempt is reported by TestNG as SKIP and the attempt that follows as PASS or FAIL, so a FAIL entry is
always a final result. Nothing here relies on the order of the entries in the file.

It also writes failed-tests.txt next to the results file: one "package.Class#method" per test that did not pass.
The next attempt of a failed CI job passes that file to "mvn test -DrerunTests=<file>" (listeners.RerunFailedTestsFilter)
so only those tests run again. No file is written when everything passed, so a later re-run is a full run.

A skipped test is listed only when its class had a failed setup method (@BeforeClass, @BeforeMethod) or it was
cut short by a failure: that skip is a consequence. A skip a test chose itself (SkipException: missing test data)
would only skip again.
"""
import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

DEFAULT_RESULTS = ["target/surefire-reports/testng-results.xml", "test-output/testng-results.xml"]
RERUN_FILE = "failed-tests.txt"
OWN_SKIP = "org.testng.SkipException"
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
    """One entry per <test> block: {name, ms, entries}; an entry is a dict of one <test-method> result."""
    blocks = []
    root = ET.parse(path).getroot()
    for test in root.iter("test"):
        entries = []
        for cls in test.iter("class"):
            for method in cls.iter("test-method"):
                exception = method.find("./exception")
                message = method.find("./exception/message")
                exception_class = exception.get("class", "") if exception is not None else ""
                entries.append({
                    "cls": cls.get("name", "?"),
                    "method": method.get("name", "?"),
                    "config": method.get("is-config") == "true",
                    "status": method.get("status", ""),
                    "params": params_of(method),
                    "exception": exception_class,
                    "message": first_line(message.text if message is not None else "") or exception_class,
                })
        blocks.append({"name": test.get("name", "?"), "ms": test.get("duration-ms", "0"), "entries": entries})
    return blocks


def judge(block):
    """Reads one <test> block into: failed setup methods, one result per test, and the tests to run again.

    Per test (class, method, data-provider row): FAIL entries are final results; a SKIP that is not a SkipException is
    an attempt that was retried. So the order of the entries does not matter, and a test that runs several times
    (invocationCount) with one failure is reported as failed, not as a retry that passed."""
    setup_failures = {}
    config_failed_classes = set()
    groups = {}
    for entry in block["entries"]:
        if entry["config"]:
            if entry["status"] == "FAIL":
                config_failed_classes.add(entry["cls"])
                found = setup_failures.setdefault((entry["cls"], entry["method"]), {"times": 0, "message": entry["message"]})
                found["times"] += 1
            continue  # setup/teardown that worked (or was skipped) says nothing
        groups.setdefault((entry["cls"], entry["method"], entry["params"]), []).append(entry)

    results, rerun = [], set()
    for (cls, method, _params), entries in groups.items():
        fails = [e for e in entries if e["status"] == "FAIL"]
        passes = [e for e in entries if e["status"] == "PASS"]
        retried = [e for e in entries if e["status"] == "SKIP" and e["exception"] != OWN_SKIP]
        own_skips = [e for e in entries if e["status"] == "SKIP" and e["exception"] == OWN_SKIP]
        name = f"{cls}.{method}"
        if fails or passes:
            flaky = min(len(passes), max(0, len(retried) - len(fails)))
            for entry in fails:
                results.append({"kind": "failed", "name": name, "message": entry["message"], "retried": bool(retried)})
            results += [{"kind": "flaky", "name": name, "message": retried[i]["message"] if i < len(retried) else ""}
                        for i in range(flaky)]
            results += [{"kind": "passed", "name": name} for _ in range(len(passes) - flaky)]
            if fails:
                rerun.add(f"{cls}#{method}")
        else:
            results.append({"kind": "skipped", "name": name})
            if cls in config_failed_classes or retried or not own_skips:
                rerun.add(f"{cls}#{method}")
    return setup_failures, results, rerun


def tests_seen(blocks):
    return {f"{e['cls']}#{e['method']}" for block in blocks for e in block["entries"] if not e["config"]}


def check_rerun(list_file, blocks):
    """The problems of a re-run that did not run what it was asked to run; empty when it did."""
    listed = {line.strip() for line in Path(list_file).read_text(encoding="utf-8").splitlines()
              if line.strip() and not line.startswith("//")}
    seen = tests_seen(blocks)
    problems = []
    if not seen:
        problems.append("No test ran at all, so nothing was verified.")
    missing = sorted(listed - seen)
    if missing:
        shown = ", ".join(missing[:10]) + (f" and {len(missing) - 10} more" if len(missing) > 10 else "")
        problems.append(f"{len(missing)} of the {len(listed)} listed test(s) did not show up in the results (renamed, "
                        f"removed, or not part of the chosen suite): {shown}")
    return problems


def write_rerun_list(results_path, tests):
    target = results_path.parent / RERUN_FILE
    if tests:
        target.write_text("\n".join(sorted(tests)) + "\n", encoding="utf-8")
    elif target.exists():
        target.unlink()  # a stale list must not outlive a green run


def main():
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    rerun_list = os.environ.get("RERUN_LIST", "").strip()
    out = open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") if os.environ.get("GITHUB_STEP_SUMMARY") \
        else sys.stdout
    # Without the results, a plain run has nothing to report; a re-run has nothing to prove its tests ran.
    failing_exit = 1 if rerun_list else 0
    paths = find_results(sys.argv[1:])
    if not paths:
        out.write("### Test results\n\nNo TestNG results were found: the run stopped before the tests started.\n")
        return failing_exit

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
        return failing_exit

    totals = {"passed": 0, "flaky": 0, "failed": 0, "skipped": 0}
    rows, failed, flaky, setup, to_rerun = [], [], [], [], set()
    for block in blocks:
        setup_failures, results, rerun = judge(block)
        to_rerun |= rerun
        counts = {"passed": 0, "flaky": 0, "failed": 0, "skipped": 0}
        for result in results:
            counts[result["kind"]] += 1
            if result["kind"] == "failed":
                failed.append((block["name"], result["name"], result["message"], result["retried"]))
            elif result["kind"] == "flaky":
                flaky.append((block["name"], result["name"], result["message"]))
        setup += [(block["name"], f"{cls}.{method}", found["times"], found["message"])
                  for (cls, method), found in setup_failures.items()]
        for kind, value in counts.items():
            totals[kind] += value
        if not results and not setup_failures:
            continue
        icon = "❌" if counts["failed"] or setup_failures else ("⚠️" if counts["skipped"] or counts["flaky"] else "✅")
        rows.append(f"| {icon} | {cell(block['name'])} | {counts['passed'] + counts['flaky']} | {counts['failed']} | "
                    f"{counts['skipped']} | {counts['flaky']} | {human(block['ms'])} |")

    total = sum(totals.values())
    icon = "❌" if totals["failed"] or setup else "✅"
    headline = (f"{total} tests, {totals['passed'] + totals['flaky']} passed, {totals['failed']} failed, "
                f"{totals['skipped']} skipped, {totals['flaky']} passed only on retry")
    if setup:
        headline += f", {len(setup)} failed setup or teardown method(s)"
    lines = [f"### {icon} Test results: {headline}", ""]
    lines += ["| | Suite | Passed | Failed | Skipped | Flaky | Time |", "|---|---|--:|--:|--:|--:|--:|"] + rows + [""]
    if setup:
        lines += ["#### Failed setup or teardown (the tests that depend on it are skipped)", "",
                  "| Suite | Method | Times | First error line |", "|---|---|--:|---|"]
        lines += [f"| {cell(suite)} | `{cell(name)}` | {times} | {cell(clip(message)) or '-'} |"
                  for suite, name, times, message in setup]
        lines.append("")
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

    problems = check_rerun(rerun_list, blocks) if rerun_list else []
    if problems:
        lines += ["#### ❌ This re-run did not run what it was asked to run", ""] + [f"- {p}" for p in problems] + [""]
    out.write("\n".join(lines) + "\n")

    write_rerun_list(readable[0], to_rerun)

    annotations = [(suite, f"{name}: setup failed {times}x: {clip(message)}") for suite, name, times, message in setup]
    annotations += [(suite, f"{name}: {clip(message)}") for suite, name, message, _retried in failed]
    for suite, text in annotations[:MAX_ANNOTATIONS]:
        print(f"::error title={escape_annotation(suite)}::{escape_annotation(text)}")
    if len(annotations) > MAX_ANNOTATIONS:
        print(f"::notice::{len(annotations) - MAX_ANNOTATIONS} more problems are listed in the job summary")
    for problem in problems:
        print(f"::error title=Re-run did not run the listed tests::{escape_annotation(problem)}")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
