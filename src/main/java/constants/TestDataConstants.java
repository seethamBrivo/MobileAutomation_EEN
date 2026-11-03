package constants;

public final class TestDataConstants {

    private TestDataConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Bridge name prefixes. Pre-test cleanup deletes bridges by prefix, so every test must
    // create its bridges through these constants and nothing else.
    public static final String BRIDGE_PREFIX_TEST = "TestBridge_";
    public static final String BRIDGE_PREFIX_DUPLICATE = "DuplicateBridge_";
    /** Short prefix of the temporary name the rename test gives an existing bridge (the name is `Rn_` + 5 digits). */
    public static final String BRIDGE_PREFIX_RENAMED = "Rn_";

    // Random suffix range for generated e-mail addresses.
    public static final int MIN_RANDOM_SUFFIX = 1000;
    public static final int MAX_RANDOM_RANGE = 9000;

    // Test-data JSON keys (credentials block).
    public static final String TEST_DATA_FILE = "testData.json";
    public static final String RESELLER_CREDS = "reseller_user_creds";
    public static final String ENDUSER_CREDS = "enduser_user_creds";
    public static final String TS02_CREDS = "ts02_user_creds";
    public static final String TEST_ACCOUNT_EMAIL = "test_account_email";

    // Test-data JSON keys (search_filter block).
    public static final String SEARCH_FILTER = "search_filter";
    public static final String VALID_SEARCH_TERM = "valid_search_term";
    public static final String INVALID_SEARCH_TERM = "invalid_search_term";
    public static final String BRIDGE_VALID_SEARCH_TERM = "bridge_valid_search_term";

    // Test-data JSON keys (timezone_data block).
    public static final String TIMEZONE_DATA = "timezone_data";
    public static final String VALID_TIMEZONE = "valid_timezone";
    public static final String TEST_TIMEZONE = "test_timezone";
    public static final String TIMEZONE_SEARCH_TERM = "search_term";
    public static final String TIMEZONE_INVALID_SEARCH_TERM = "invalid_search_term";

    // Test-data JSON keys (layout block).
    public static final String LAYOUT_DATA = "layout_data";
    public static final String LAYOUT_NAME = "layout_name";
    public static final String ASPECT_RATIO = "aspect_ratio";
    public static final String MAX_CAMERAS_PER_ROW = "max_cameras_per_row";
    public static final String SHOW_CAMERA_TITLE_BARS = "show_camera_title_bars";

    // Test-data JSON keys (mechanical_zoom_camera block).
    public static final String MECHANICAL_ZOOM_CAMERA_DATA = "mechanical_zoom_camera_data";
    public static final String MECHANICAL_ZOOM_CAMERA_NAME = "camera_name";

    // Test-data JSON keys (archive_search block).
    public static final String ARCHIVE_SEARCH_DATA = "archive_search_data";
    public static final String ARCHIVE_VALID_SEARCH_TERM = "valid_search_term";
    public static final String ARCHIVE_NO_RESULTS_SEARCH_TERM = "no_results_search_term";
}
