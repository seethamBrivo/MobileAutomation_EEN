package constants;

import java.time.Duration;

public final class FrameworkConstants {

    public static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);
    public static final long EXPLICIT_WAIT_SECONDS = 20;
    public static final String CONFIG_FILE = "config.properties";
    public static final String ANDROID_CONFIG_FILE = "android.properties";
    public static final String IOS_CONFIG_FILE = "ios.properties";
    public static final String REPORT_DIRECTORY = "test-output/extent-reports";

    private FrameworkConstants() {
    }
}