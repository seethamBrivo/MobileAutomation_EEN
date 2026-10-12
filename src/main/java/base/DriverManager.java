package base;

import constants.FrameworkConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import utils.BrowserStackSessionNameHelper;
import utils.ConfigReader;

public final class DriverManager {

    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initializeDriver() {
        initializeDriver(null, null, new Object[0]);
    }

    public static void initializeDriver(String testClassName, String testMethodName) {
        initializeDriver(testClassName, testMethodName, new Object[0]);
    }

    /**
     * The session name the user configured, captured before the first test overwrites the
     * {@code browserstackSessionName} system property with its own resolved name. Re-reading the
     * property per test would treat that resolved name as configured and give every session the
     * first test's name.
     */
    private static final String CONFIGURED_SESSION_NAME = ConfigReader.getProperty("browserstackSessionName");

    public static void initializeDriver(String testClassName, String testMethodName, Object[] testParameters) {
        if (DRIVER.get() != null) {
            return;
        }

        String platform = ConfigReader.getPlatform();
        String provider = getExecutionProvider();
        String browserStackSessionName = BrowserStackSessionNameHelper.resolveSessionName(
                CONFIGURED_SESSION_NAME,
                testClassName,
                testMethodName,
                testParameters);
        System.setProperty("browserstackSessionName", browserStackSessionName);

        try {
            if ("android".equalsIgnoreCase(platform)) {
                DRIVER.set(createAndroidDriver());
            } else if ("ios".equalsIgnoreCase(platform)) {
                DRIVER.set(createIosDriver());
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }
            // Zero: WaitUtils owns all polling. See FrameworkConstants.IMPLICIT_WAIT.
            DRIVER.get().manage().timeouts().implicitlyWait(FrameworkConstants.IMPLICIT_WAIT);
            LOGGER.info("Appium driver initialized for platform: {} using provider: {} with session name: {}",
                    platform, provider, browserStackSessionName);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Failed to create Appium driver", exception);
        }
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver has not been initialized. Call initializeDriver() first.");
        }
        return driver;
    }

    public static byte[] getScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Ends the session. The ThreadLocal is always cleared, even when quit() throws
     * (BrowserStack idle timeout, session already gone); otherwise the dead driver would be
     * handed to every following test on this thread.
     */
    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
            LOGGER.info("Appium driver session ended");
        } catch (WebDriverException e) {
            LOGGER.warn("driver.quit() failed, discarding session anyway: {}", e.getMessage());
        } finally {
            DRIVER.remove();
        }
    }

    private static AndroidDriver createAndroidDriver() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(ConfigReader.getProperty("deviceName"))
                .setPlatformVersion(ConfigReader.getProperty("platformVersion"))
                .setAutomationName(ConfigReader.getProperty("automationName"))
                .setApp(resolveAppReference());

        // For Local execution, set UDID if provided. Ignore for BrowserStack.
        if (!isBrowserStackExecution()) {
            String udid = ConfigReader.getProperty("udid");
            if (isValidProperty(udid)) {
                options.setUdid(udid);
            }
        }

        setCommonMobileOptions(options);
        setRemoteProviderOptions(options);

        String appPackage = ConfigReader.getProperty("appPackage");
        String appActivity = ConfigReader.getProperty("appActivity");

        // For both BrowserStack and Local: Set appPackage and appWaitActivity
        // Using appWaitActivity (instead of appActivity) allows flexible launch while still ensuring consistent starting state
        if (isValidProperty(appPackage)) {
            options.setAppPackage(appPackage);
        }
        if (isValidProperty(appActivity)) {
            // Use appWaitActivity instead of appActivity to avoid hard-fail if activity is wrong/unexported
            options.setAppWaitActivity(appActivity);
        }

        // Android 15 workaround: Force app to stop and restart for each session (applies to both BrowserStack and local)
        options.amend("forceAppLaunch", true);
        options.amend("shouldTerminateApp", true);

        return new AndroidDriver(getServerUrl(), options);
    }

    private static IOSDriver createIosDriver() throws MalformedURLException {
        String udid = ConfigReader.getProperty("udid");
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(ConfigReader.getProperty("deviceName"))
                .setPlatformVersion(ConfigReader.getProperty("platformVersion"))
                .setAutomationName(ConfigReader.getProperty("automationName"))
                .setApp(resolveAppReference());

        setCommonMobileOptions(options);
        setRemoteProviderOptions(options);

        if (isValidProperty(udid)) {
            options.setUdid(udid);
        }

        String bundleId = ConfigReader.getProperty("bundleId");
        if (isValidProperty(bundleId)) {
            options.setBundleId(bundleId);
        }

        return new IOSDriver(getServerUrl(), options);
    }

    private static void setCommonMobileOptions(io.appium.java_client.remote.options.BaseOptions<?> options) {
        String noReset = ConfigReader.getProperty("noReset");
        String fullReset = ConfigReader.getProperty("fullReset");
        String newCommandTimeout = ConfigReader.getProperty("newCommandTimeout");

        // Default is noReset=true when the property is absent. This differs from Appium's own default
        // (false). Login state therefore survives between sessions unless BaseTest.tearDown logs out
        // or clears the app, which it does. Documented in the *.properties.example files.
        if (isValidProperty(noReset)) {
            options.setNoReset(Boolean.parseBoolean(noReset));
        } else {
            // Consistent default for both environments
            options.setNoReset(true);
            LOGGER.info("Using default noReset=true to keep app installed between tests (state cleared via forceAppLaunch)");
        }

        if (isValidProperty(fullReset)) {
            options.amend("fullReset", Boolean.parseBoolean(fullReset));
        } else {
            // Consistent default for both environments
            options.amend("fullReset", false);
        }

        if (isValidProperty(newCommandTimeout)) {
            try {
                options.amend("newCommandTimeout", Integer.parseInt(newCommandTimeout));
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid newCommandTimeout value '{}', using default (600s): {}", newCommandTimeout, e.getMessage());
                options.amend("newCommandTimeout", 600);
            }
        }
    }

    private static void setRemoteProviderOptions(io.appium.java_client.remote.options.BaseOptions<?> options) {
        if (!isBrowserStackExecution()) {
            return;
        }

        Map<String, Object> browserStackOptions = new LinkedHashMap<>();
        browserStackOptions.put("userName", requireProperty("browserstackUserName"));
        browserStackOptions.put("accessKey", requireProperty("browserstackAccessKey"));

        String projectName = ConfigReader.getProperty("browserstackProjectName");
        if (isValidProperty(projectName)) {
            browserStackOptions.put("projectName", projectName);
        }

        String buildName = ConfigReader.getProperty("browserstackBuildName");
        if (isValidProperty(buildName)) {
            browserStackOptions.put("buildName", buildName);
        }

        String sessionName = ConfigReader.getProperty("browserstackSessionName");
        if (isValidProperty(sessionName)) {
            browserStackOptions.put("sessionName", sessionName);
        }

        String debug = ConfigReader.getProperty("browserstackDebug");
        if (isValidProperty(debug)) {
            browserStackOptions.put("debug", Boolean.parseBoolean(debug));
        }

        String networkLogs = ConfigReader.getProperty("browserstackNetworkLogs");
        if (isValidProperty(networkLogs)) {
            browserStackOptions.put("networkLogs", Boolean.parseBoolean(networkLogs));
        }

        String idleTimeout = ConfigReader.getProperty("browserstackIdleTimeout");
        if (isValidProperty(idleTimeout)) {
            try {
                browserStackOptions.put("idleTimeout", Integer.parseInt(idleTimeout));
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid browserstackIdleTimeout value '{}', using default (90s): {}", idleTimeout, e.getMessage());
                browserStackOptions.put("idleTimeout", 90);
            }
        }

        options.setCapability("bstack:options", browserStackOptions);
    }

    private static String resolveAppReference() {
        String providerSpecificApp = ConfigReader.getProperty("browserstackApp");
        String configuredApp = ConfigReader.getProperty("app");

        // If executing on BrowserStack, prioritize browserstackApp / bs:// URI
        if (isBrowserStackExecution()) {
            if (isValidProperty(providerSpecificApp)) {
                return providerSpecificApp;
            } else if (configuredApp != null && configuredApp.startsWith("bs://")) {
                return configuredApp;
            } else {
                throw new IllegalArgumentException("BrowserStack execution requires a 'bs://' app URL specified in 'browserstackApp' or 'app'.");
            }
        }

        // Local execution path logic
        if (!isValidProperty(configuredApp)) {
            throw new IllegalArgumentException("Missing app configuration for the selected platform");
        }

        Path appPath = Path.of(configuredApp);
        if (appPath.isAbsolute()) {
            return appPath.toString();
        }

        return Path.of(System.getProperty("user.dir"), configuredApp).toString();
    }

    private static String getExecutionProvider() {
        String provider = ConfigReader.getProperty("executionProvider");
        return isValidProperty(provider) ? provider.trim().toLowerCase() : "local";
    }

    private static boolean isBrowserStackExecution() {
        return "browserstack".equalsIgnoreCase(getExecutionProvider());
    }

    private static String requireProperty(String key) {
        String value = ConfigReader.getProperty(key);
        if (!isValidProperty(value)) {
            throw new IllegalArgumentException("Missing required configuration: " + key);
        }
        return value;
    }

    /** ConfigReader never returns null (missing keys come back as ""), so blank is the only "unset". */
    private static boolean isValidProperty(String value) {
        return value != null && !value.isBlank();
    }

    private static URL getServerUrl() throws MalformedURLException {
        if (isBrowserStackExecution()) {
            return URI.create(requireProperty("browserstackHubUrl")).toURL();
        }
        return URI.create(ConfigReader.getProperty("appiumServerUrl")).toURL();
    }
}
