package base;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BrowserStackReporterTest {

    private final List<String> scripts = new ArrayList<>();
    private final JavascriptExecutor executor = new JavascriptExecutor() {
        @Override
        public Object executeScript(String script, Object... args) {
            scripts.add(script);
            return null;
        }

        @Override
        public Object executeAsyncScript(String script, Object... args) {
            return null;
        }
    };

    @BeforeMethod
    public void onBrowserStack() {
        System.setProperty("executionProvider", "browserstack");
        scripts.clear();
    }

    @AfterMethod(alwaysRun = true)
    public void restore() {
        System.clearProperty("executionProvider");
    }

    /** A TestNG result is a large interface; only what the reporter reads is answered. */
    private static ITestResult result(String method, int status, boolean retried, Throwable error) {
        ITestNGMethod testMethod = (ITestNGMethod) Proxy.newProxyInstance(ITestNGMethod.class.getClassLoader(),
                new Class<?>[]{ITestNGMethod.class}, (p, m, a) -> m.getName().equals("getMethodName") ? method : null);
        return (ITestResult) Proxy.newProxyInstance(ITestResult.class.getClassLoader(), new Class<?>[]{ITestResult.class},
                (p, m, a) -> switch (m.getName()) {
                    case "getMethod" -> testMethod;
                    case "getStatus" -> status;
                    case "wasRetried" -> retried;
                    case "getThrowable" -> error;
                    default -> null;
                });
    }

    private String lastStatusScript() {
        return scripts.stream().filter(s -> s.contains("setSessionStatus")).reduce((a, b) -> b).orElse("");
    }

    @Test
    public void noRecordedResultsIsNotAGreenSession() {
        new BrowserStackReporter().finishSession(executor);
        String script = lastStatusScript();
        assertTrue(script.contains("\"status\": \"failed\""), script);
        assertTrue(script.contains("No test results were recorded"), script);
    }

    @Test
    public void allPassedIsGreenWithCounts() {
        BrowserStackReporter reporter = new BrowserStackReporter();
        reporter.testStarted(executor, "a");
        reporter.record(executor, result("a", ITestResult.SUCCESS, false, null));
        reporter.record(executor, result("b", ITestResult.SUCCESS, false, null));
        reporter.finishSession(executor);
        String script = lastStatusScript();
        assertTrue(script.contains("\"status\": \"passed\""), script);
        assertTrue(script.contains("2 passed, 0 failed of 2"), script);
    }

    @Test
    public void aFailureOrNotRunTestMakesTheSessionFailed() {
        BrowserStackReporter reporter = new BrowserStackReporter();
        reporter.record(executor, result("a", ITestResult.SUCCESS, false, null));
        reporter.record(executor, result("b", ITestResult.FAILURE, false, new AssertionError("boom\nsecond line")));
        reporter.record(executor, result("c", ITestResult.SKIP, false, null));
        reporter.finishSession(executor);
        String script = lastStatusScript();
        assertTrue(script.contains("\"status\": \"failed\""), script);
        assertTrue(script.contains("1 passed, 1 failed, 1 not run of 3"), script);
        assertTrue(script.contains("FAILED: b") && script.contains("NOT RUN: c"), script);
    }

    @Test
    public void aRetriedFailureIsOverwrittenByTheRetryOutcome() {
        BrowserStackReporter reporter = new BrowserStackReporter();
        // TestNG reports the attempt it will retry as SKIP + wasRetried; it must not count as a failed or skipped test.
        reporter.record(executor, result("a", ITestResult.SKIP, true, new RuntimeException("flaky")));
        reporter.record(executor, result("a", ITestResult.SUCCESS, false, null));
        reporter.finishSession(executor);
        assertTrue(lastStatusScript().contains("1 passed, 0 failed of 1"), lastStatusScript());
        assertEquals(scripts.stream().filter(s -> s.contains("ATTEMPT FAILED, retrying a")).count(), 1);
    }

    @Test
    public void nothingIsSentOutsideBrowserStack() {
        System.clearProperty("executionProvider");
        System.setProperty("executionProvider", "local");
        BrowserStackReporter reporter = new BrowserStackReporter();
        reporter.testStarted(executor, "a");
        reporter.record(executor, result("a", ITestResult.SUCCESS, false, null));
        reporter.finishSession(executor);
        assertTrue(scripts.isEmpty(), scripts.toString());
    }
}
