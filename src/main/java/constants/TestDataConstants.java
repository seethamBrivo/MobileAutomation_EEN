package constants;

public final class TestDataConstants {
    private TestDataConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // Bridge test prefixes
    public static final String BRIDGE_PREFIX_TEST = "TestBridge_";
    public static final String BRIDGE_PREFIX_DUPLICATE = "DuplicateBridge_";
    public static final String BRIDGE_PREFIX_FILTER = "FilterTestBridge_";
    public static final String BRIDGE_PREFIX_CONTEXT_MENU = "ContextMenuBridge_";
    public static final String BRIDGE_PREFIX_UPDATED = "UpdatedBridge_";
    public static final String BRIDGE_PREFIX_RESTART = "RestartBridge_";

    // Random number generation for emails
    public static final int MIN_RANDOM_SUFFIX = 1000;
    public static final int MAX_RANDOM_RANGE = 9000;
}
