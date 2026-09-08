package constants;

import java.time.Duration;

public final class TimeoutConstants {
    private TimeoutConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final Duration QUICK_CHECK_TIMEOUT = Duration.ofMillis(200);
    public static final Duration POPUP_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration SHORT_TIMEOUT = Duration.ofSeconds(2);
    public static final Duration SWIPE_PAUSE = Duration.ofMillis(300);
    public static final Duration SWIPE_MOVE = Duration.ofMillis(700);
    public static final Duration LONG_PRESS = Duration.ofMillis(2000);

    public static final int PROCESS_TIMEOUT_SECONDS = 10;
    public static final int BROWSERSTACK_TIMEOUT_SECONDS = 600;
    public static final int COMMAND_TIMEOUT_SECONDS = 90;
}
