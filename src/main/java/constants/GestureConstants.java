package constants;

public final class GestureConstants {

    private GestureConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final double SWIPE_START_PERCENT_VERTICAL = 0.8;
    public static final double SWIPE_END_PERCENT_VERTICAL = 0.2;

    /** Upper bound on swipes when scrolling to bring an element into view. */
    public static final int MAX_SCROLL_SWIPES = 4;
}
