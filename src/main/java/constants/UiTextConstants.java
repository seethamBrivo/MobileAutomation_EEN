package constants;

/**
 * UI text constants from the mobile app. These are actual strings displayed in the UI,
 * not test data keys or configuration values.
 */
public final class UiTextConstants {

    private UiTextConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Camera search and filter UI text
    public static final String SEARCH_PLACEHOLDER = "Filter by camera";
    public static final String EMPTY_STATE_MESSAGE = "No matching cameras";
    public static final String CAMERA_COUNTER_TEXT = " camera";
}
