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

    /** How long a list screen may show loading skeletons before its first rows arrive. */
    public static final Duration LIST_LOAD_TIMEOUT = Duration.ofSeconds(30);

    /** How long a details screen may show loading skeletons before it is closed and re-opened. */
    public static final Duration DETAILS_LOAD_TIMEOUT = Duration.ofSeconds(15);

    /** Pause before a scrolling lookup is repeated, so a screen still on skeleton rows can finish loading. */
    public static final Duration SCROLL_RETRY_SETTLE = Duration.ofSeconds(8);

    /** Budget for a list to re-render after typing into its filter field. */
    public static final Duration FILTER_RESULT_TIMEOUT = Duration.ofSeconds(5);

    /** Budget for "is a back button visible" probes when the screen may already be at the top. */
    public static final Duration BACK_BUTTON_CHECK_TIMEOUT = Duration.ofSeconds(1);

    public static final Duration SWIPE_PAUSE = Duration.ofMillis(300);
    public static final Duration SWIPE_MOVE = Duration.ofMillis(700);
    public static final Duration LONG_PRESS = Duration.ofMillis(2000);
}
