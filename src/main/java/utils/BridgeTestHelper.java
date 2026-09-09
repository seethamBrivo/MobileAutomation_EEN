package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.DevicesPage;

/** Bridge cleanup shared by the bridge tests' @BeforeMethod / @AfterMethod paths. */
public final class BridgeTestHelper {

    private static final Logger logger = LogManager.getLogger(BridgeTestHelper.class);

    /** Safety valve so a misbehaving filter cannot loop forever. */
    private static final int MAX_LEFTOVERS_PER_PREFIX = 5;

    private BridgeTestHelper() {
    }

    /**
     * Deletes every bridge whose name starts with {@code prefix}, i.e. leftovers from earlier
     * failed runs. Must be called from the Bridges tab. Never throws: cleanup problems are
     * logged and the test decides what to do when it cannot add its own bridge.
     *
     * @return number of bridges deleted
     */
    public static int deleteLeftoverBridges(DevicesPage devicesPage, String prefix) {
        int deleted = 0;
        try {
            while (deleted < MAX_LEFTOVERS_PER_PREFIX && devicesPage.tryDeleteBridge(prefix, false)) {
                deleted++;
                // After a delete the app returns to the Bridges tab; the filter is re-typed by tryDeleteBridge.
            }
        } catch (Exception e) {
            logger.warn("Leftover cleanup for prefix '{}' stopped after {} deletion(s): {}", prefix, deleted, e.getMessage());
        }
        if (deleted > 0) {
            logger.info("Pre-test: deleted {} leftover bridge(s) with prefix '{}'", deleted, prefix);
        } else {
            logger.info("Pre-test: no leftover bridges with prefix '{}'", prefix);
        }
        return deleted;
    }
}
