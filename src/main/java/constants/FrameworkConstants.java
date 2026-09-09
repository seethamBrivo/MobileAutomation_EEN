package constants;

import java.time.Duration;

public final class FrameworkConstants {

    /**
     * Implicit wait is deliberately zero. Every lookup goes through {@link utils.WaitUtils},
     * which owns polling via explicit waits. A non-zero implicit wait stacks on top of every
     * explicit-wait poll and makes "element absent" probes take tens of seconds.
     */
    public static final Duration IMPLICIT_WAIT = Duration.ZERO;

    /** Default explicit wait for elements that are expected to appear. */
    public static final Duration EXPLICIT_WAIT = Duration.ofSeconds(20);

    public static final String CONFIG_FILE = "config.properties";
    public static final String ANDROID_CONFIG_FILE = "android.properties";
    public static final String IOS_CONFIG_FILE = "ios.properties";
    public static final String REPORT_DIRECTORY = "test-output/extent-reports";

    private FrameworkConstants() {
    }
}
