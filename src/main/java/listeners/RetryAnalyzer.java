package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigReader;

/**
 * Re-runs a failed {@code @Test} (fresh Appium session: {@code @BeforeMethod}/{@code @AfterMethod}
 * run again around every attempt). The number of <em>extra</em> attempts comes from
 * {@code -DretryCount=N} (or {@code retryCount} in config.properties); default 1, 0 disables.
 *
 * <p>TestNG does not retry failed configuration methods; a failing login is covered by
 * {@code LoginPage.loginAs}'s own retry. Applied to every test by {@link RetryTransformer}.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    static final int DEFAULT_RETRY_COUNT = 1;

    private int retriesUsed = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = maxRetries();
        if (retriesUsed >= maxRetries) {
            return false;
        }
        retriesUsed++;
        logger.warn("Retrying {} (retry {}/{}) after: {}", result.getMethod().getMethodName(), retriesUsed,
                maxRetries, result.getThrowable() == null ? "unknown failure" : result.getThrowable().toString());
        return true;
    }

    static int maxRetries() {
        String configured = System.getProperty("retryCount", "").trim();
        if (configured.isBlank()) {
            try {
                configured = ConfigReader.getProperty("retryCount");
            } catch (RuntimeException e) {
                // No config.properties (e.g. a bare local run): TestNG would silently swallow this
                // and never retry, so fall back to the default instead.
                logger.debug("Could not read retryCount from config: {}", e.toString());
            }
        }
        if (configured.isBlank()) {
            return DEFAULT_RETRY_COUNT;
        }
        try {
            return Math.max(0, Integer.parseInt(configured.trim()));
        } catch (NumberFormatException e) {
            logger.warn("Invalid retryCount '{}', using default {}", configured, DEFAULT_RETRY_COUNT);
            return DEFAULT_RETRY_COUNT;
        }
    }
}
