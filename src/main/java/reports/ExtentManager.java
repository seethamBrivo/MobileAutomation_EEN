package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import constants.FrameworkConstants;
import java.nio.file.Path;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import utils.ConfigReader;

public final class ExtentManager {

    private static final ExtentReports EXTENT_REPORTS = new ExtentReports();
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();
    private static boolean initialized;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getReporter() {
        if (!initialized) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String platform = ConfigReader.getPlatform().toLowerCase(Locale.ROOT);
            Path reportPath = Path.of(FrameworkConstants.REPORT_DIRECTORY,
                    "ExecutionReport_" + platform + "_" + timestamp + ".html");
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath.toString());
            sparkReporter.config().setDocumentTitle("Mobile Automation Report - " + platform.toUpperCase(Locale.ROOT));
            sparkReporter.config().setReportName("Appium Test Execution - " + platform.toUpperCase(Locale.ROOT));
            EXTENT_REPORTS.attachReporter(sparkReporter);
            EXTENT_REPORTS.setSystemInfo("Framework", "Appium + TestNG + Maven");
            EXTENT_REPORTS.setSystemInfo("Platform", platform.toUpperCase(Locale.ROOT));
            initialized = true;
        }
        return EXTENT_REPORTS;
    }

    public static void createTest(String testName, String description, String platform, String dataLabel) {
        ExtentTest extentTest = getReporter().createTest(testName);
        if (description != null && !description.isBlank()) {
            extentTest.assignCategory(description);
        }
        if (platform != null && !platform.isBlank()) {
            extentTest.assignDevice(platform.toUpperCase(Locale.ROOT));
            extentTest.info("Platform: " + platform.toUpperCase(Locale.ROOT));
        }
        if (dataLabel != null && !dataLabel.isBlank()) {
            extentTest.info("Data Set: " + dataLabel);
        }
        EXTENT_TEST.set(extentTest);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static synchronized void flushReports() {
        if (initialized) {
            EXTENT_REPORTS.flush();
        }
        EXTENT_TEST.remove();
    }
}
