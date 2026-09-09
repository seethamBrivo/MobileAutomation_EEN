package utils;
import constants.FrameworkConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
public final class ConfigReader {
    private static final Properties PROPERTIES = new Properties();
    private static boolean loaded;
    private ConfigReader() {
    }
    public static synchronized void loadProperties() {
        if (loaded) {
            return;
        }
        loadIntoProperties(FrameworkConstants.CONFIG_FILE);
        loadIntoProperties(getPlatformConfigFile());
        loaded = true;
    }
    public static String getProperty(String key) {
        String systemValue = System.getProperty(key, "").trim();
        if (!systemValue.isBlank()) {
            return systemValue;
        }
        loadProperties();
        return PROPERTIES.getProperty(key, "").trim();
    }
    public static String getPlatform() {
        String platformFromSystem = System.getProperty("platform", "").trim();
        if (!platformFromSystem.isBlank()) {
            return platformFromSystem;
        }
        if (!loaded) {
            loadIntoProperties(FrameworkConstants.CONFIG_FILE);
        }
        return PROPERTIES.getProperty("platform", "android").trim();
    }
    private static String getPlatformConfigFile() {
        return switch (getPlatform().toLowerCase()) {
            case "android" -> FrameworkConstants.ANDROID_CONFIG_FILE;
            case "ios" -> FrameworkConstants.IOS_CONFIG_FILE;
            default -> throw new IllegalArgumentException("Unsupported platform configured");
        };
    }
    private static void loadIntoProperties(String fileName) {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalArgumentException("Unable to locate configuration file: " + fileName);
            }
            PROPERTIES.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load configuration file: " + fileName, exception);
        }
    }
}
