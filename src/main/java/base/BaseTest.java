package base;

import io.appium.java_client.AppiumDriver;
import com.google.gson.JsonObject;
import pages.ResellerDashboardPage;
import pages.LoginPage;
import utils.JsonDataReader;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected AppiumDriver driver;
    protected final Logger logger = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, ITestResult testResult) {
        DriverManager.initializeDriver(
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                testResult.getParameters());
        driver = DriverManager.getDriver();
        logger.info("Test setup completed for {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());

        // Handle notification permission popup (BrowserStack)
        handleNotificationPermission();
    }

    private void handleNotificationPermission() {
        try {
            // Wait a bit for popup to appear
            Thread.sleep(2000);

            // Try different locators for "Allow" button
            try {
                // Try 1: Using resource-id (most common)
                driver.findElement(io.appium.java_client.AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button")).click();
                logger.info("Clicked 'Allow' on notification permission popup (using resource-id)");
            } catch (Exception e1) {
                try {
                    // Try 2: Using text "Allow"
                    driver.findElement(io.appium.java_client.AppiumBy.xpath("//android.widget.Button[@text='Allow']")).click();
                    logger.info("Clicked 'Allow' on notification permission popup (using text)");
                } catch (Exception e2) {
                    try {
                        // Try 3: Using text "ALLOW" (uppercase)
                        driver.findElement(io.appium.java_client.AppiumBy.xpath("//android.widget.Button[@text='ALLOW']")).click();
                        logger.info("Clicked 'Allow' on notification permission popup (using uppercase text)");
                    } catch (Exception e3) {
                        // No permission popup appeared or already handled
                        logger.info("No notification permission popup found (might be already handled or not shown)");
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted while waiting for permission popup");
        }
    }
    
    
    protected ResellerDashboardPage loginAs(String accountType) {

        JsonObject testData =
                JsonDataReader.readJsonObject("testData.json");

        JsonObject credentials = testData
                .getAsJsonObject("credentials")
                .getAsJsonObject(accountType);

        String username = credentials
                .get("username")
                .getAsString();

        String password = credentials
                .get("password")
                .getAsString();

        logger.info("Logging in using account type: {}", accountType);

        LoginPage loginPage = new LoginPage(driver);

        return loginPage.loginAs(username, password);
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        if (driver != null) {
            // WORKAROUND: Force app restart by closing and reopening
            // This ensures next test starts from login screen
            try {
                // Option 1: Try logout (if working)
                ResellerDashboardPage dashboardPage = new ResellerDashboardPage(driver);
                dashboardPage.logout();
                logger.info("Successfully logged out after test");
            } catch (Exception e) {
                logger.warn("Logout method failed, using force restart: {}", e.getMessage());

                // Option 2: Force stop app (Android 15 workaround)
                try {
                    Runtime.getRuntime().exec("adb -s 8XUW9LWWZ9MFOZ4D shell am force-stop com.een.vms.android");
                    logger.info("App force stopped - will restart fresh for next test");
                } catch (Exception ex) {
                    logger.warn("Could not force stop app: {}", ex.getMessage());
                }
            }

            try {
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                if (testResult.getStatus() == ITestResult.SUCCESS) {
                    jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Test passed successfully\"}}");
                } else if (testResult.getStatus() == ITestResult.FAILURE) {
                    String reason = testResult.getThrowable() != null ? testResult.getThrowable().getMessage() : "Test failed";
                    if (reason != null) {
                        reason = reason.replaceAll("[\"\\\r\n]", " ");
                    }
                    jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"" + reason + "\"}}");
                }
            } catch (Exception e) {
                logger.warn("Could not update BrowserStack session status: {}", e.getMessage());
            }
        }

        DriverManager.quitDriver();
        logger.info("Test teardown completed for {}", getClass().getSimpleName());
    }
}