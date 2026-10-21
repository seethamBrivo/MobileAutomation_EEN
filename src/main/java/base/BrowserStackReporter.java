package base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import utils.BrowserStackStatus;
import utils.ConfigReader;

/**
 * What one class session tells BrowserStack: a marker on the session timeline at the start and end of every test, and
 * one pass/fail status with a summary line for the whole session. Holds the outcome of each test run in the session.
 * One instance per test class (a session belongs to one class), used from that class's thread only.
 */
final class BrowserStackReporter {

    private static final String PASSED = "PASSED";
    private static final String FAILED = "FAILED";
    private static final String NOT_RUN = "NOT_RUN";

    /**
     * Final outcome of each test in this session. A failed attempt that TestNG retries is overwritten by the retry's
     * outcome; NOT_RUN is a test skipped because a setup method failed.
     */
    private final Map<String, String> outcomes = new LinkedHashMap<>();
    private long testStartedAt;

    static boolean isBrowserStackExecution() {
        return "browserstack".equalsIgnoreCase(ConfigReader.getProperty("executionProvider"));
    }

    static String firstLine(String text) {
        return text == null ? "" : text.lines().findFirst().orElse("");
    }

    void testStarted(JavascriptExecutor driver, String testName) {
        testStartedAt = System.nanoTime();
        annotate(driver, "START " + testName, "info");
    }

    /** Records the test's outcome and marks it on the BrowserStack timeline, so a shared session stays readable. */
    void record(JavascriptExecutor driver, ITestResult testResult) {
        String name = testResult.getMethod().getMethodName();
        long seconds = Math.max(0, (System.nanoTime() - testStartedAt) / 1_000_000_000L);
        String reason = testResult.getThrowable() == null ? "" : ": " + firstLine(testResult.getThrowable().getMessage());
        switch (testResult.getStatus()) {
            case ITestResult.SUCCESS -> {
                outcomes.put(name, PASSED);
                annotate(driver, "PASSED " + name + " (" + seconds + "s)", "info");
            }
            case ITestResult.FAILURE -> {
                outcomes.put(name, FAILED);
                annotate(driver, "FAILED " + name + " (" + seconds + "s)" + reason, "error");
            }
            case ITestResult.SKIP -> {
                if (testResult.wasRetried()) {
                    // TestNG reports a failed attempt it is about to retry as SKIP; the retry decides the outcome.
                    annotate(driver, "ATTEMPT FAILED, retrying " + name + " (" + seconds + "s)" + reason, "warn");
                } else {
                    // The test never ran (a @BeforeMethod/@BeforeClass failed); it must not leave the session looking green.
                    outcomes.put(name, NOT_RUN);
                    annotate(driver, "NOT RUN " + name + " (setup failed)" + reason, "error");
                }
            }
            default -> { }
        }
    }

    /** Sets the one status of the session: passed only if tests ran and every one of them passed. */
    void finishSession(JavascriptExecutor driver) {
        if (!isBrowserStackExecution()) {
            return;
        }
        List<String> failed = namesWithOutcome(FAILED);
        List<String> notRun = namesWithOutcome(NOT_RUN);
        if (outcomes.isEmpty()) {
            // Nothing was recorded: the tests were skipped before running (a @BeforeClass failed). That must not show up
            // as a green session.
            BrowserStackStatus.setStatus(driver, false, "No test results were recorded; setup failed before any test ran");
        } else {
            BrowserStackStatus.setStatus(driver, failed.isEmpty() && notRun.isEmpty(),
                    BrowserStackStatus.sessionReason(outcomes.size(), failed, notRun));
        }
    }

    void reset() {
        outcomes.clear();
    }

    private void annotate(JavascriptExecutor driver, String message, String level) {
        if (isBrowserStackExecution() && driver != null) {
            BrowserStackStatus.annotate(driver, message, level);
        }
    }

    private List<String> namesWithOutcome(String outcome) {
        return outcomes.entrySet().stream().filter(e -> outcome.equals(e.getValue())).map(Map.Entry::getKey).toList();
    }
}
