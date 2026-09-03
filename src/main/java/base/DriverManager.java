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

    public static void initializeDriver(String testClassName, String testMethodName, Object[] testParameters) {
        if (DRIVER.get() != null) {
            return;
        }

        String platform = ConfigReader.getPlatform();
        String provider = getExecutionProvider();
        String browserStackSessionName = BrowserStackSessionNameHelper.resolveSessionName(
                ConfigReader.getProperty("browserstackSessionName"),
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

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            LOGGER.info("Appium driver session ended");
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
            if (udid != null && !udid.isBlank()) {
                options.setUdid(udid);
            }
        }

        setCommonMobileOptions(options);
        setRemoteProviderOptions(options);

        String appPackage = ConfigReader.getProperty("appPackage");
        String appActivity = ConfigReader.getProperty("appActivity");

        // For BrowserStack: Don't set appActivity/appWaitActivity - let app launch naturally
        // For Local: Only set appPackage, use appWaitActivity for targeted wait (safer than appActivity)
        if (isBrowserStackExecution()) {
            // BrowserStack: Only set package, let it launch naturally without waiting for specific activity
            if (appPackage != null && !appPackage.isBlank()) {
                options.setAppPackage(appPackage);
            }
            // Don't set appActivity or appWaitActivity - permission popups can block activity detection
        } else {
            // Local execution: Set package and wait activity (not appActivity to avoid hard-fail)
            if (appPackage != null && !appPackage.isBlank()) {
                options.setAppPackage(appPackage);
            }
            // Use appWaitActivity instead of appActivity to avoid hard-fail if activity is wrong/unexported
            if (appActivity != null && !appActivity.isBlank()) {
                options.setAppWaitActivity(appActivity);
            }
            // Android 15 workaround: Force app to stop and restart for each session
            options.amend("forceAppLaunch", true);
            options.amend("shouldTerminateApp", true);
        }

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

        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        String bundleId = ConfigReader.getProperty("bundleId");
        if (bundleId != null && !bundleId.isBlank()) {
            options.setBundleId(bundleId);
        }

        return new IOSDriver(getServerUrl(), options);
    }

    private static void setCommonMobileOptions(io.appium.java_client.remote.options.BaseOptions<?> options) {
        String noReset = ConfigReader.getProperty("noReset");
        String fullReset = ConfigReader.getProperty("fullReset");
        String newCommandTimeout = ConfigReader.getProperty("newCommandTimeout");

        // Both BrowserStack and Local should respect config settings
        // Default to noReset=true for BrowserStack if not specified (keeps app installed for speed)
        if (isBrowserStackExecution()) {
            if (noReset != null && !noReset.isBlank()) {
                options.setNoReset(Boolean.parseBoolean(noReset));
            } else {
                // Default for BrowserStack
                options.setNoReset(true);
                LOGGER.info("BrowserStack: Using default noReset=true to keep app installed between tests");
            }
            if (fullReset != null && !fullReset.isBlank()) {
                options.amend("fullReset", Boolean.parseBoolean(fullReset));
            } else {
                options.amend("fullReset", false);
            }
        } else {
            // Local execution follows config file settings
            if (noReset != null && !noReset.isBlank()) {
                options.setNoReset(Boolean.parseBoolean(noReset));
            }
            if (fullReset != null && !fullReset.isBlank()) {
                options.amend("fullReset", Boolean.parseBoolean(fullReset));
            }
        }

        if (newCommandTimeout != null && !newCommandTimeout.isBlank()) {
            options.amend("newCommandTimeout", Integer.parseInt(newCommandTimeout));
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
        if (projectName != null && !projectName.isBlank()) {
            browserStackOptions.put("projectName", projectName);
        }

        String buildName = ConfigReader.getProperty("browserstackBuildName");
        if (buildName != null && !buildName.isBlank()) {
            browserStackOptions.put("buildName", buildName);
        }

        String sessionName = ConfigReader.getProperty("browserstackSessionName");
        if (sessionName != null && !sessionName.isBlank()) {
            browserStackOptions.put("sessionName", sessionName);
        }

        String debug = ConfigReader.getProperty("browserstackDebug");
        if (debug != null && !debug.isBlank()) {
            browserStackOptions.put("debug", Boolean.parseBoolean(debug));
        }

        String networkLogs = ConfigReader.getProperty("browserstackNetworkLogs");
        if (networkLogs != null && !networkLogs.isBlank()) {
            browserStackOptions.put("networkLogs", Boolean.parseBoolean(networkLogs));
        }

        String idleTimeout = ConfigReader.getProperty("browserstackIdleTimeout");
        if (idleTimeout != null && !idleTimeout.isBlank()) {
            browserStackOptions.put("idleTimeout", Integer.parseInt(idleTimeout));
        }

        options.setCapability("bstack:options", browserStackOptions);
    }

   private static String resolveAppReference() {
        String providerSpecificApp = ConfigReader.getProperty("browserstackApp");
        String configuredApp = ConfigReader.getProperty("app");

        // If executing on BrowserStack, prioritize browserstackApp / bs:// URI
        if (isBrowserStackExecution()) {
            if (providerSpecificApp != null && !providerSpecificApp.isBlank()) {
                return providerSpecificApp;
            } else if (configuredApp != null && configuredApp.startsWith("bs://")) {
                return configuredApp;
            } else {
                throw new IllegalArgumentException("BrowserStack execution requires a 'bs://' app URL specified in 'browserstackApp' or 'app'.");
            }
        }

        // Local execution path logic
        if (configuredApp == null || configuredApp.isBlank()) {
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
        return (provider == null || provider.isBlank()) ? "local" : provider.trim().toLowerCase();
    }

    private static boolean isBrowserStackExecution() {
        return "browserstack".equalsIgnoreCase(getExecutionProvider());
    }

    private static String requireProperty(String key) {
        String value = ConfigReader.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required configuration: " + key);
        }
        return value;
    }

    private static URL getServerUrl() throws MalformedURLException {
        if (isBrowserStackExecution()) {
            return URI.create(requireProperty("browserstackHubUrl")).toURL();
        }
        return URI.create(ConfigReader.getProperty("appiumServerUrl")).toURL();
    }
}