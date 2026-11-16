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
    /** Temporary name given to an existing camera by the rename test. */
    public static final String CAMERA_PREFIX_RENAMED = "RnCam_";

    // Random suffix range for generated e-mail addresses.
    public static final int MIN_RANDOM_SUFFIX = 1000;
    public static final int MAX_RANDOM_RANGE = 9000;

    // Test-data JSON keys (credentials block).
    public static final String TEST_DATA_FILE = "testData.json";
    public static final String RESELLER_CREDS = "reseller_user_creds";
    public static final String ENDUSER_CREDS = "enduser_user_creds";
    public static final String TS02_CREDS = "ts02_user_creds";
    public static final String RESELLER3_CREDS = "reseller3_user_creds";
    public static final String TEST_ACCOUNT_EMAIL = "test_account_email";

    // Test-data JSON keys (mechanical_zoom_camera block).
    public static final String MECHANICAL_ZOOM_CAMERA_DATA = "mechanical_zoom_camera_data";
    public static final String MECHANICAL_ZOOM_CAMERA_NAME = "camera_name";

    // Test-data JSON keys (switchable_camera block): an online camera the End User can turn off and on.
    public static final String SWITCHABLE_CAMERA_DATA = "switchable_camera_data";
    public static final String SWITCHABLE_CAMERA_NAME = "camera_name";

    // Test-data JSON keys (archive_search block).
    public static final String ARCHIVE_SEARCH_DATA = "archive_search_data";
    public static final String ARCHIVE_VALID_SEARCH_TERM = "valid_search_term";
    public static final String ARCHIVE_NO_RESULTS_SEARCH_TERM = "no_results_search_term";
    // A large, old Archive folder and the video in it (1.5 MB): found by search, and used only to cancel a download.
    public static final String ARCHIVE_CANCEL_FOLDER_SEARCH_TERM = "cancel_folder_search_term";
    public static final String ARCHIVE_CANCEL_FILE_SEARCH_TERM = "cancel_file_search_term";
}
