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

    // History browser event filter sheet
    public static final String EVENT_FILTERS_TITLE = "Event filters";
    public static final String TIMELINE_EVENTS_HEADER = "TIMELINE EVENTS";
    public static final String SELECT_ALL_LABEL = "Select";
    public static final String DESELECT_ALL_LABEL = "Deselect";

    // Archive downloads: Android saves to Files, iOS to Photos, and each says so when the download is done.
    public static final String ARCHIVE_DOWNLOAD_SAVED_MESSAGE =
            utils.PlatformLocator.isIos() ? "Saved to Photos" : "Saved to Files";
}
