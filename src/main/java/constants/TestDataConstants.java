package constants;

public final class TestDataConstants {

    private TestDataConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Bridge name prefixes. Pre-test cleanup deletes bridges by prefix, so every test must
    // create its bridges through these constants and nothing else.
    public static final String BRIDGE_PREFIX_TEST = "TestBridge_";
    public static final String BRIDGE_PREFIX_DUPLICATE = "DuplicateBridge_";
    public static final String BRIDGE_PREFIX_FILTER = "FilterTestBridge_";
    public static final String BRIDGE_PREFIX_CONTEXT_MENU = "ContextMenuBridge_";
    public static final String BRIDGE_PREFIX_UPDATED = "UpdatedBridge_";

    // Random suffix range for generated e-mail addresses.
    public static final int MIN_RANDOM_SUFFIX = 1000;
    public static final int MAX_RANDOM_RANGE = 9000;

    // Test-data JSON keys (credentials block).
    public static final String TEST_DATA_FILE = "testData.json";
    public static final String RESELLER_CREDS = "reseller_user_creds";
    public static final String ENDUSER_CREDS = "enduser_user_creds";

    // Test-data JSON keys (search_filter block).
    public static final String SEARCH_FILTER = "search_filter";
    public static final String VALID_SEARCH_TERM = "valid_search_term";
    public static final String INVALID_SEARCH_TERM = "invalid_search_term";
}
