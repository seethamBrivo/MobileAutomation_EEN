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

    // Archive downloads: Android saves to Files and iOS to Photos, and each says so when the download is done.
    public static final String ARCHIVE_SAVED_MESSAGE_ANDROID = "Saved to Files";
    public static final String ARCHIVE_SAVED_MESSAGE_IOS = "Saved to Photos";

    /** Fragments of the Archive snackbar, compared in lower case. */
    public static final String ARCHIVE_DOWNLOADING_MESSAGE = "downloading";
    public static final String ARCHIVE_CANCELLED_MESSAGE = "download cancelled";

    public static final String USER_ADDED_MESSAGE = "User was added";

    // iOS New user form: what Add shows under the e-mail and the empty name fields (BrowserStack iOS hierarchy).
    public static final String IOS_INVALID_EMAIL_MESSAGE = "Please enter valid data";
    public static final String FIELD_REQUIRED_MESSAGE = "Field is required";
}
