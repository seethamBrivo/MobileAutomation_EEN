package listeners;

import base.DriverManager;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriverException;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reports.ExtentManager;
import utils.ConfigReader;

public class TestListener implements ITestListener, IConfigurationListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    private String getPlatform() {
        return ConfigReader.getPlatform();
    }

    private String buildDataLabel(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters == null || parameters.length == 0) {
            return "No data provider values";
        }
        return Arrays.stream(parameters)
                .map(value -> value == null ? "null" : value.toString())
                .collect(Collectors.joining(" | "));
    }

    private String buildDisplayName(ITestResult result) {
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "UnknownTest";
        return methodName + " [platform=" + getPlatform() + "] [data=" + buildDataLabel(result) + "]";
    }

    private void ensureTestNode(ITestResult result) {
        if (ExtentManager.getTest() == null) {
            String description = result.getMethod() != null ? result.getMethod().getDescription() : null;
            ExtentManager.createTest(buildDisplayName(result), description, getPlatform(), buildDataLabel(result));
        }
    }

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getReporter();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.createTest(buildDisplayName(result), result.getMethod().getDescription(),
                getPlatform(), buildDataLabel(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logFailure(result);
    }

    private void logFailure(ITestResult result) {
        ensureTestNode(result);
        savePageSource(result);
        byte[] screenshot;
        try {
            screenshot = DriverManager.getScreenshot();
        } catch (IllegalStateException | WebDriverException exception) {
            // The driver may not exist yet (a configuration method failed before setup completed) or the
            // device session may already be gone (BrowserStack timed it out). An exception escaping a
            // listener aborts the whole TestNG run, so record the failure without a screenshot instead.
            logger.warn("No screenshot for {}: {}", result.getMethod().getMethodName(),
                    String.valueOf(exception.getMessage()).lines().findFirst().orElse(""));
            ExtentManager.getTest().fail(result.getThrowable());
            return;
        }
        // Isolated from the Extent attachment below: Allure has no other usage in this codebase,
        // so a failure here must not take down the screenshot-on-failure that Extent's report relies on.
        try {
            Allure.addAttachment(result.getMethod().getMethodName() + " - screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), "png");
        } catch (RuntimeException exception) {
            logger.warn("Could not attach screenshot to Allure: {}", exception.toString());
        }
        String screenshotBase64 = Base64.getEncoder().encodeToString(screenshot);
        ExtentManager.getTest().fail(result.getThrowable(),
                MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotBase64, result.getMethod().getMethodName()).build());
    }

    /**
     * A failed @BeforeMethod (login, navigation) skips the test and would otherwise leave no screenshot at
     * all, so a login that never completes could not be diagnosed from the report.
     */
    @Override
    public void onConfigurationFailure(ITestResult result) {
        logFailure(result);
    }


    /**
     * Writes the UI hierarchy of the failing screen to target/page-sources/, so a locator that stopped matching
     * (typically after an app update) can be fixed from the real attributes instead of guessing. Never throws:
     * the session may already be gone.
     */
    private void savePageSource(ITestResult result) {
        try {
            String xml = DriverManager.getDriver().getPageSource();
            Path dir = Path.of("target", "page-sources");
            Files.createDirectories(dir);
            Path file = dir.resolve("FAILED-" + result.getMethod().getMethodName() + "-"
                    + System.currentTimeMillis() + ".xml");
            Files.writeString(file, xml);
            logger.info("Saved page source of the failing screen to {}", file);
        } catch (Exception exception) {
            logger.debug("No page source for {}: {}", result.getMethod().getMethodName(), exception.toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.wasRetried()) {
            // TestNG reports a failed attempt that will be retried as SKIP; keep its screenshot and error.
            logFailure(result);
            ExtentManager.getTest().log(Status.WARNING, "Attempt failed; the test is being retried");
            return;
        }
        ensureTestNode(result);
        ExtentManager.getTest().log(Status.SKIP, result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flushReports();
    }
}
