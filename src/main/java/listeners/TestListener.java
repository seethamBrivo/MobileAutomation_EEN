package listeners;

import base.DriverManager;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reports.ExtentManager;
import utils.ConfigReader;

public class TestListener implements ITestListener {

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
        ensureTestNode(result);
        try {
            String screenshotBase64 = Base64.getEncoder().encodeToString(DriverManager.getScreenshot());
            ExtentManager.getTest().fail(result.getThrowable(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotBase64, result.getMethod().getMethodName()).build());
        } catch (IllegalStateException exception) {
            // Driver may not be available when a configuration method fails before setup completes.
            ExtentManager.getTest().fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ensureTestNode(result);
        ExtentManager.getTest().log(Status.SKIP, result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flushReports();
    }
}