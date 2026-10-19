package utils;

import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

/** BrowserStack session status and timeline annotations, sent through {@code browserstack_executor}. */
public final class BrowserStackStatus {

    private static final Logger LOGGER = LogManager.getLogger(BrowserStackStatus.class);
    /** BrowserStack truncates longer reasons. */
    private static final int MAX_TEXT_LENGTH = 250;

    private BrowserStackStatus() {
    }

    public static void setStatus(JavascriptExecutor executor, boolean passed, String reason) {
        run(executor, statusScript(passed, reason), "set session status");
    }

    /** Adds a marker to the session's text-log timeline, so test boundaries show up in a shared session. */
    public static void annotate(JavascriptExecutor executor, String message) {
        annotate(executor, message, "info");
    }

    /** {@code level} is one of BrowserStack's: info, warn, error, debug. */
    public static void annotate(JavascriptExecutor executor, String message, String level) {
        run(executor, annotateScript(message, level), "annotate");
    }

    /**
     * One-line summary for a session that ran several tests, e.g.
     * {@code "5 passed, 1 failed of 6 | FAILED: test03_verifyBridges..."}. BrowserStack cuts the reason
     * at about 250 characters, so the failed names come last and are what gets truncated.
     */
    public static String sessionReason(int total, Collection<String> failedTests, Collection<String> skippedTests) {
        int failed = failedTests.size();
        int skipped = skippedTests.size();
        int passed = total - failed - skipped;
        StringBuilder reason = new StringBuilder();
        reason.append(passed).append(" passed, ").append(failed).append(" failed");
        if (skipped > 0) {
            reason.append(", ").append(skipped).append(" not run");
        }
        reason.append(" of ").append(total);
        if (failed > 0) {
            reason.append(" | FAILED: ").append(String.join(", ", failedTests));
        }
        if (skipped > 0) {
            reason.append(" | NOT RUN: ").append(String.join(", ", skippedTests));
        }
        return reason.toString();
    }

    static String statusScript(boolean passed, String reason) {
        return "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \""
                + (passed ? "passed" : "failed") + "\", \"reason\": \"" + sanitize(reason) + "\"}}";
    }

    static String annotateScript(String message) {
        return annotateScript(message, "info");
    }

    static String annotateScript(String message, String level) {
        return "browserstack_executor: {\"action\": \"annotate\", \"arguments\": {\"data\": \""
                + sanitize(message) + "\", \"level\": \"" + sanitize(level) + "\"}}";
    }

    /** Keeps the text valid inside the hand-built JSON string: no quotes, backslashes or line breaks. */
    static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        String clean = text.replaceAll("[\"\\\\\r\n]", " ").trim();
        return clean.length() > MAX_TEXT_LENGTH ? clean.substring(0, MAX_TEXT_LENGTH) : clean;
    }

    private static void run(JavascriptExecutor executor, String script, String what) {
        try {
            executor.executeScript(script);
        } catch (RuntimeException e) {
            LOGGER.warn("Could not {} on BrowserStack: {}", what, e.toString());
        }
    }
}
