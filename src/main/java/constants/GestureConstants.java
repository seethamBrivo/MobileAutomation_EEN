package constants;

public final class GestureConstants {

    private GestureConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final double SWIPE_START_PERCENT_VERTICAL = 0.8;
    public static final double SWIPE_END_PERCENT_VERTICAL = 0.2;

    /**
     * Swipe down inside a list, from 40% to 85% of the height. A plain swipe down starts at 20%, over the filter
     * chip row of the Cameras list, and would scroll that row instead of the list.
     */
    public static final double LIST_SWIPE_DOWN_START_PERCENT = 0.4;
    public static final double LIST_SWIPE_DOWN_END_PERCENT = 0.85;

    /** Upper bound on swipes when scrolling to bring an element into view. */
    public static final int MAX_SCROLL_SWIPES = 4;

    /** Finger distance from the pinch centre as a share of the target's height: apart / together. */
    public static final double PINCH_WIDE_SPREAD_PERCENT = 0.35;
    public static final double PINCH_NARROW_SPREAD_PERCENT = 0.08;
    /** Size of the native pinch as a share of the element, for the UiAutomator2 fallback gesture. */
    public static final double NATIVE_PINCH_PERCENT = 0.7;
    /**
     * Lowest point, as a share of the screen height, where an Android long press is made. The centre of a card at
     * the bottom of a list (a switched-off camera sorts last) is about 95% down, and a press there opens nothing
     * (measured on BrowserStack, Samsung Galaxy S25 Ultra); the same card pressed at 93% opens its menu.
     */
    public static final double LONG_PRESS_LOWEST_POINT_PERCENT = 0.93;
}
