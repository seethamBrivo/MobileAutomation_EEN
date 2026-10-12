package listeners;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/** Pure unit test (no device): how {@code -DretryCount} is interpreted. */
public class RetryAnalyzerTest {

    private static final String KEY = "retryCount";

    @AfterMethod(alwaysRun = true)
    public void clearProperty() {
        System.clearProperty(KEY);
    }

    @Test(description = "unset retryCount falls back to the default")
    public void defaultWhenNothingSet() {
        System.clearProperty(KEY);
        assertEquals(RetryAnalyzer.maxRetries(), RetryAnalyzer.DEFAULT_RETRY_COUNT);
    }

    @Test(description = "0 disables retries")
    public void zeroDisablesRetries() {
        System.setProperty(KEY, "0");
        assertEquals(RetryAnalyzer.maxRetries(), 0);
    }

    @Test(description = "a positive value is used as is")
    public void positiveValueIsUsed() {
        System.setProperty(KEY, "2");
        assertEquals(RetryAnalyzer.maxRetries(), 2);
    }

    @Test(description = "a non-numeric value falls back to the default")
    public void invalidValueFallsBackToDefault() {
        System.setProperty(KEY, "abc");
        assertEquals(RetryAnalyzer.maxRetries(), RetryAnalyzer.DEFAULT_RETRY_COUNT);
    }

    @Test(description = "a negative value is clamped to 0, not treated as unlimited")
    public void negativeValueIsClampedToZero() {
        System.setProperty(KEY, "-1");
        assertEquals(RetryAnalyzer.maxRetries(), 0);
    }
}
