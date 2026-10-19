package base;

import constants.FrameworkConstants;
import io.appium.java_client.AppiumClientConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.Setting;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
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
    /** Default for UiAutomator2's wait-for-idle before each command; see {@link #applyIdleTimeoutSetting}. */
    private static final long DEFAULT_IDLE_TIMEOUT_MS = 1000;
    /** Longest a single driver command (including session creation) may wait for the device's answer. */
    private static final Duration COMMAND_READ_TIMEOUT = Duration.ofSeconds(180);

    private DriverManager() {
    }

    public static void initializeDriver() {
        initializeDriver(null, null, new Object[0]);
    }

    public static void initializeDriver(String testClassName, String testMethodName) {
        initializeDriver(testClassName, testMethodName, new Object[0]);
    }

    /**
     * The session name the user configured. The resolved per-session name is passed to the capability
     * builders directly: a JVM-wide system property would let two parallel sessions overwrite each
     * other's name.
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

        try {
            if ("android".equalsIgnoreCase(platform)) {
                DRIVER.set(createAndroidDriver(browserStackSessionName));
            } else if ("ios".equalsIgnoreCase(platform)) {
                DRIVER.set(createIosDriver(browserStackSessionName));
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }
            // Zero: WaitUtils owns all polling. See FrameworkConstants.IMPLICIT_WAIT.
            DRIVER.get().manage().timeouts().implicitlyWait(FrameworkConstants.IMPLICIT_WAIT);
            applyIdleTimeoutSetting(DRIVER.get());
            LOGGER.info("Appium driver initialized for platform: {} using provider: {} with session name: {}",
                    platform, provider, browserStackSessionName);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Failed to create Appium driver", exception);
        }
    }


    /**
     * UiAutomator2 waits (10 s by default) for the screen to go "idle" before it answers each command. A screen
     * of live camera previews never goes idle, so single lookups were measured at 10-20 s each. Switching the wait
     * off entirely (0) removed that, but let steps race screen transitions (the add-bridge "+" tap landed before the
     * list had finished loading, typing was cut short). So a short bounded wait is used instead: 1 s caps the
     * stall on busy screens and still lets ordinary screens settle. Set {@code waitForIdleTimeoutMs} to change it;
     * 0 turns the wait off, -1 keeps the driver's own default.
     */
    private static void applyIdleTimeoutSetting(AppiumDriver driver) {
        String configured = ConfigReader.getProperty("waitForIdleTimeoutMs");
        long millis = DEFAULT_IDLE_TIMEOUT_MS;
        if (isValidProperty(configured)) {
            try {
                millis = Long.parseLong(configured.trim());
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid waitForIdleTimeoutMs '{}', using {}", configured, DEFAULT_IDLE_TIMEOUT_MS);
            }
        }
        if (millis < 0) {
            return;
        }
        try {
            driver.setSetting(Setting.WAIT_FOR_IDLE_TIMEOUT, millis);
            LOGGER.info("UiAutomator2 waitForIdleTimeout set to {} ms", millis);
        } catch (WebDriverException e) {
            LOGGER.warn("Could not set waitForIdleTimeout ({}); the driver default applies",
                    String.valueOf(e.getMessage()).lines().findFirst().orElse(""));
        }
    }

    /** True when this thread has a driver and the Appium session still answers. */
    public static boolean isSessionAlive() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            return false;
        }
        try {
            driver.manage().window().getSize();
            return true;
        } catch (WebDriverException e) {
            return false;
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

    private static AndroidDriver createAndroidDriver(String sessionName) throws MalformedURLException {
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
        setRemoteProviderOptions(options, sessionName);

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

        // The client's default read timeout is far longer than any real command. A BrowserStack session that
        // stops answering (seen once, after a click) then blocked the whole run for ~10 minutes.
        AppiumClientConfig clientConfig = AppiumClientConfig.defaultConfig()
                .baseUrl(getServerUrl())
                .readTimeout(COMMAND_READ_TIMEOUT);
        return new AndroidDriver(clientConfig, options);
    }

    private static IOSDriver createIosDriver(String sessionName) throws MalformedURLException {
        String udid = ConfigReader.getProperty("udid");
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(ConfigReader.getProperty("deviceName"))
                .setPlatformVersion(ConfigReader.getProperty("platformVersion"))
                .setAutomationName(ConfigReader.getProperty("automationName"))
                .setApp(resolveAppReference());

        setCommonMobileOptions(options);
        setRemoteProviderOptions(options, sessionName);

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

    private static void setRemoteProviderOptions(io.appium.java_client.remote.options.BaseOptions<?> options,
                                                 String sessionName) {
        if (!isBrowserStackExecution()) {
            return;
        }
        if (isBrowserStackSdkMode()) {
            // The BrowserStack SDK injects credentials, project/build/session names and the
            // debug/network-log flags from browserstack.yml; setting them here would be ignored at
            // best and would need credentials that the SDK run keeps in the environment.
            LOGGER.info("BrowserStack SDK mode: bstack:options are supplied by browserstack.yml");
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

        // Video recording and the debug/network logs all slow a session down; BrowserStack rates them
        // as high-impact capabilities. Leave browserstackVideo unset to keep BrowserStack's default.
        String video = ConfigReader.getProperty("browserstackVideo");
        if (isValidProperty(video)) {
            browserStackOptions.put("video", Boolean.parseBoolean(video));
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

    /** True when the run was started with {@code -DbrowserstackSdk=true} (the SDK javaagent is attached). */
    private static boolean isBrowserStackSdkMode() {
        return "true".equalsIgnoreCase(System.getProperty("browserstackSdk", "").trim());
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
