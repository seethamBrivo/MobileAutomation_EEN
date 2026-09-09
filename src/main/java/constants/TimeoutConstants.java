package constants;

import java.time.Duration;

/**
 * Timeouts for things that are optional or expected to be absent. Anything that must appear
 * uses {@link FrameworkConstants#EXPLICIT_WAIT}.
 */
public final class TimeoutConstants {

    private TimeoutConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    /** Budget for optional system / biometrics popups that may never appear. */
    public static final Duration POPUP_TIMEOUT = Duration.ofSeconds(5);

    /** Budget for "is this optional element on screen?" probes (snackbars, status labels). */
    public static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);

    /** Budget for a list to re-render after typing into its filter field. */
    public static final Duration FILTER_RESULT_TIMEOUT = Duration.ofSeconds(5);

    public static final Duration SWIPE_PAUSE = Duration.ofMillis(300);
    public static final Duration SWIPE_MOVE = Duration.ofMillis(700);
    public static final Duration LONG_PRESS = Duration.ofMillis(2000);
}
