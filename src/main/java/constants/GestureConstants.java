package constants;

public final class GestureConstants {

    private GestureConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final double SWIPE_START_PERCENT_VERTICAL = 0.8;
    public static final double SWIPE_END_PERCENT_VERTICAL = 0.2;

    public static final double LIST_SWIPE_DOWN_START_PERCENT = 0.4;
    public static final double LIST_SWIPE_DOWN_END_PERCENT = 0.85;

    /** Upper bound on swipes when scrolling to bring an element into view. */
    public static final int MAX_SCROLL_SWIPES = 4;

    /** Finger distance from the pinch centre as a share of the target's height: apart / together. */
    public static final double PINCH_WIDE_SPREAD_PERCENT = 0.35;
    public static final double PINCH_NARROW_SPREAD_PERCENT = 0.08;
}
