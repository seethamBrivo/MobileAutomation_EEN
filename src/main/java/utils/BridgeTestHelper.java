package utils;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import pages.DevicesPage;

public final class BridgeTestHelper {
    private BridgeTestHelper() {
    }
    public static void deleteBridgeIfExists(
            AppiumDriver driver,
            DevicesPage deleteBridgePage,
            String bridgeNamePattern,
            Logger logger
    ) {
        if (driver == null || deleteBridgePage == null || bridgeNamePattern == null || logger == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            logger.info("Pre-test: Checking if bridge exists: {}", bridgeNamePattern);
            boolean bridgeFound = deleteBridgePage.enterBridgeNameInFilter(bridgeNamePattern);
            if (!bridgeFound) {
                logger.info("Pre-test: Bridge {} does not exist or already cleaned up", bridgeNamePattern);
                return;
            }

            // Verify the bridge name matches before deleting (prevent accidental deletion of production bridges)
            String displayedBridgeName = deleteBridgePage.getFirstBridgeName();
            if (!displayedBridgeName.contains(bridgeNamePattern)) {
                logger.warn("Pre-test: Bridge name mismatch - Expected pattern: {}, Found: {}. Skipping deletion for safety.",
                    bridgeNamePattern, displayedBridgeName);
                return;
            }

            deleteBridgePage.waitForFirstBridgeToBeClickable();
            deleteBridgePage.clickFirstBridge();
            GestureUtils.swipeUpTwice(driver);
            deleteBridgePage.clickDeleteBridgeButton();
            deleteBridgePage.clickDeleteButton();
            if (deleteBridgePage.isBridgeDeletedMessageDisplayed()) {
                logger.info("Pre-test: Cleaned up existing bridge: {} - Success message confirmed", bridgeNamePattern);
            } else {
                logger.info("Pre-test: Bridge deletion completed for: {} (no success message)", bridgeNamePattern);
            }
        } catch (Exception e) {
            logger.info("Pre-test: Bridge {} does not exist or already cleaned up", bridgeNamePattern);
        }
    }
    public static void cleanupBridge(
            AppiumDriver driver,
            DevicesPage deleteBridgePage,
            String bridgeName,
            Logger logger
    ) {
        if (driver == null || deleteBridgePage == null || bridgeName == null) {
            if (logger != null) {
                logger.warn("Cleanup: Cannot proceed - null parameters detected");
            }
            return;
        }
        if (logger == null) {
            // Cannot log if logger is null, silently return
            return;
        }
        try {
            logger.info("@AfterMethod: Cleaning up test bridge: {}", bridgeName);
            boolean bridgeFound = deleteBridgePage.enterBridgeNameInFilter(bridgeName);
            if (!bridgeFound) {
                logger.info("Cleanup: Bridge {} not found - may have been deleted in test", bridgeName);
                return;
            }

            // Verify the bridge name matches before deleting (prevent accidental deletion of production bridges)
            String displayedBridgeName = deleteBridgePage.getFirstBridgeName();
            if (!displayedBridgeName.equals(bridgeName)) {
                logger.warn("Cleanup: Bridge name mismatch - Expected: {}, Found: {}. Skipping deletion for safety.",
                    bridgeName, displayedBridgeName);
                return;
            }

            deleteBridgePage.waitForFirstBridgeToBeClickable();
            deleteBridgePage.clickFirstBridge();
            GestureUtils.swipeUpTwice(driver);
            deleteBridgePage.clickDeleteBridgeButton();
            deleteBridgePage.clickDeleteButton();
            logger.info("Cleanup: Test bridge deleted in @AfterMethod");
        } catch (Exception e) {
            logger.info("Cleanup: Could not delete test bridge - {}", e.getMessage());
        }
    }
}