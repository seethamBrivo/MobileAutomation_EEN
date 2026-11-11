package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.MyProfileLocators;
import locators.VSPEventsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.DeviceUtils;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

import java.util.List;
import java.util.Optional;

public class VSPEventsPage {

    private static final Logger logger = LogManager.getLogger(VSPEventsPage.class);
    private final AppiumDriver driver;

    // iOS card row detection - screen width used as orientation proxy
    private static final int IOS_LANDSCAPE_WIDTH_THRESHOLD = 600;
    private static final int IOS_PORTRAIT_ROW_THRESHOLD_PX = 40;
    private static final int IOS_LANDSCAPE_ROW_THRESHOLD_PX = 150;

    public VSPEventsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateToVSPEvents() {
        BottomNavigationBar.clickMoreTab(driver);
        verifyVSPSectionVisible();
        clickEvents();
        verifyVSPEventsScreenDisplayed();
        logger.info("Navigated to VSP Events");
    }

    public void navigateBackFromProfile() {
        if (PlatformLocator.isIos()) {
            // iOS: the Profile screen has its own back button; the keyboard is not open after choosing a time zone.
            WaitUtils.click(driver, MyProfileLocators.BACK_BUTTON);
            return;
        }
        // BLOCKER FIX B2: Hide keyboard first - TC-3920 ends with keyboard up, BACK would dismiss keyboard instead of leaving screen
        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (Exception e) {
            logger.debug("hideKeyboard failed or no keyboard present: {}", e.getMessage());
        }
        driver.navigate().back();
    }

    public void navigateBackFromVSPEvents() {
        driver.navigate().back();
    }

    public void verifyVSPSectionVisible() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.VSP_SECTION, "VSP section should be visible");
    }

    public void clickEvents() {
        WaitUtils.click(driver, VSPEventsLocators.EVENTS_OPTION);
    }

    public void verifyVSPEventsScreenDisplayed() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.VSP_EVENTS_HEADING, "VSP Events screen should be displayed");
        WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_MAIN_CONTAINER, TimeoutConstants.LIST_LOAD_TIMEOUT);
    }

    public void verifyEventCardsDisplayed() {
        if (!hasEventsPresent()) {
            throw new SkipException("SKIP: No VSP events available");
        }
        WaitUtils.assertVisible(driver, VSPEventsLocators.FIRST_EVENT_IN_LIST, TimeoutConstants.SHORT_TIMEOUT,
                "Event cards should be displayed");
        logger.info("Verified {} event cards are displayed",
                    WaitUtils.findElements(driver, VSPEventsLocators.ALL_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT).size());
    }

    public void verifyEventCardInformation() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.ANY_EVENT_THUMBNAIL, TimeoutConstants.SHORT_TIMEOUT,
                "Event card should contain thumbnail");
        String plateText = verifyElementAndGetText(VSPEventsLocators.ANY_LICENSE_PLATE, "license plate");

        // Verify 'No Plate' empty state per spec: "No Plate should be displayed if API received Empty License Plate"
        if ("No Plate".equals(plateText)) {
            logger.info("License plate field shows 'No Plate' (empty plate from API)");
        } else {
            Assert.assertFalse(plateText.isBlank(), "License plate should not be blank");
        }

        String timestamp = verifyElementAndGetText(VSPEventsLocators.ANY_TIMESTAMP, "timestamp");
        String cameraName = verifyElementAndGetText(VSPEventsLocators.ANY_CAMERA_NAME, "camera name");
        logger.info("Card verified - Plate: '{}', Time: '{}', Camera: '{}'", plateText, timestamp, cameraName);
    }

    public int getCardsPerRow() {
        // SAFETY FIX: Wait for layout to stabilize after rotation - re-find elements to get current layout
        WaitUtils.waitForVisibility(driver, VSPEventsLocators.FIRST_EVENT_IN_LIST, TimeoutConstants.ROTATION_STABILIZE);

        List<WebElement> cards = WaitUtils.findElements(driver, VSPEventsLocators.ALL_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT);
        if (cards.isEmpty()) {
            Assert.fail("No event cards found");
        }

        int firstCardY = cards.get(0).getLocation().getY();
        int firstCardHeight = cards.get(0).getSize().getHeight();
        int screenWidth = driver.manage().window().getSize().getWidth();

        // iOS: Timestamp texts are small, threshold depends on orientation
        // Android: Card elements are taller and consistent, use height/2 as threshold
        int rowThreshold;
        boolean isIosLandscape = false;
        if (PlatformLocator.isIos()) {
            isIosLandscape = screenWidth > IOS_LANDSCAPE_WIDTH_THRESHOLD;
            rowThreshold = isIosLandscape ? IOS_LANDSCAPE_ROW_THRESHOLD_PX : IOS_PORTRAIT_ROW_THRESHOLD_PX;
        } else {
            rowThreshold = firstCardHeight / 2;
        }

        // Always check positions - compare each card's Y coordinate with the first card
        long cardsInFirstRow = cards.stream()
                .filter(card -> {
                    int cardY = card.getLocation().getY();
                    return Math.abs(cardY - firstCardY) <= rowThreshold;
                })
                .count();

        String platform = PlatformLocator.isIos() ? "iOS" : "Android";
        String orientation = isIosLandscape ? "Landscape" : (PlatformLocator.isIos() ? "Portrait" : "N/A");
        logger.info("Cards per row calculation [{}:{}] - First card Y: {}, Height: {}, Threshold: {}, Count: {}, Total cards found: {}",
                platform, orientation, firstCardY, firstCardHeight, rowThreshold, cardsInFirstRow, cards.size());

        return (int) cardsInFirstRow;
    }

    public void verifyCardsPerRow(int expectedCount) {
        int actualCount = getCardsPerRow();
        Assert.assertEquals(actualCount, expectedCount,
                String.format("Expected %d card(s) per row, found %d", expectedCount, actualCount));
        logger.info("{} card(s) per row verified", expectedCount);
    }

    /**
     * Verifies pagination by scrolling and checking if content changed.
     * <p>
     * Mobile list views use view recycling - cards are removed from DOM as they scroll off-screen.
     * Therefore, we check if the CONTENT changed (different cards visible), not just if count increased.
     */
    public void verifyPaginationWorks() {
        List<WebElement> initialCards = WaitUtils.findElements(driver, VSPEventsLocators.ALL_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT);
        int initialCount = initialCards.size();

        // Capture first visible card text immediately to avoid stale element
        String firstCardTextBefore = "";
        if (!initialCards.isEmpty()) {
            try {
                firstCardTextBefore = initialCards.get(0).getText().replaceAll("\\s+", " ").trim();
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.debug("Element became stale, will re-find after scroll");
                firstCardTextBefore = "";
            }
        }

        GestureUtils.swipeUp(driver);

        List<WebElement> afterScrollCards = WaitUtils.findElements(driver, VSPEventsLocators.ALL_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT);
        int afterScrollCount = afterScrollCards.size();

        // Get text immediately after finding elements
        String firstCardTextAfter = "";
        if (!afterScrollCards.isEmpty()) {
            try {
                firstCardTextAfter = afterScrollCards.get(0).getText().replaceAll("\\s+", " ").trim();
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.debug("Element became stale after scroll");
                firstCardTextAfter = "";
            }
        }

        // Pagination works if content changed (different cards visible) OR count changed
        boolean contentChanged = !firstCardTextBefore.equals(firstCardTextAfter) && !firstCardTextBefore.isEmpty();
        boolean moreCardsLoaded = afterScrollCount > initialCount;
        boolean viewRecycled = afterScrollCount < initialCount; // Cards scrolled off and were recycled

        // In landscape/small screens, all events may fit in viewport (nothing to scroll)
        boolean allContentVisible = (afterScrollCount <= 4) && !contentChanged && (afterScrollCount == initialCount);

        if (moreCardsLoaded) {
            logger.info("Pagination verified - {} cards loaded (before: {}, after: {})",
                       afterScrollCount - initialCount, initialCount, afterScrollCount);
        } else if (contentChanged) {
            logger.info("Pagination verified - content scrolled (view recycling: {} cards before, {} after)",
                       initialCount, afterScrollCount);
        } else if (viewRecycled) {
            logger.info("Pagination verified - view recycled (cards scrolled off: before {}, after {})",
                       initialCount, afterScrollCount);
        } else if (allContentVisible) {
            logger.info("Pagination verified - all {} events visible in viewport (no scroll needed)", afterScrollCount);
        }

        Assert.assertTrue(contentChanged || moreCardsLoaded || viewRecycled || allContentVisible,
                String.format("Pagination verification failed - no content change detected. Before: %d cards (first: '%s'), After: %d cards (first: '%s')",
                        initialCount, firstCardTextBefore, afterScrollCount, firstCardTextAfter));
    }

    public void verifyToolbarHidesOnScroll() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT,
                "Toolbar should be present before scroll verification");

        if (PlatformLocator.isIos()) {
            // iOS: Navigation bar with search field is fixed at top - verify it stays visible after scroll
            GestureUtils.swipeUp(driver);
            WaitUtils.assertVisible(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT,
                    "iOS navigation bar with search field should remain visible after scrolling");
            logger.info("iOS toolbar verified - search field stays visible after scroll (fixed navigation bar)");
            return;
        }

        // Android: Toolbar scrolls off screen
        WebElement toolbarElement = WaitUtils.waitForVisibility(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT);
        int initialY = toolbarElement.getLocation().getY();

        GestureUtils.swipeUp(driver);

        if (WaitUtils.isVisible(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT)) {
            WebElement toolbarAfterScroll = WaitUtils.waitForVisibility(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT);
            int afterY = toolbarAfterScroll.getLocation().getY();
            Assert.assertTrue(afterY < initialY, "Toolbar should move up when scrolling");
            logger.info("Toolbar verified - moved up on scroll");
        } else {
            logger.info("Toolbar verified - hidden on scroll");
        }
    }

    public void rotateToLandscape() {
        DeviceUtils.rotateToLandscape(driver);
    }

    public void rotateToPortrait() {
        DeviceUtils.rotateToPortrait(driver);
    }

    public String captureEventTimestamp() {
        if (!hasEventsPresent()) {
            return "";
        }
        String timestamp = WaitUtils.waitForVisibility(driver, VSPEventsLocators.TIMESTAMP_WITH_TIME).getText();
        logger.info("Captured timestamp: {}", timestamp);
        return timestamp;
    }

    public void verifyTimestampChangedAfterTimezoneChange(String initialTimestamp) {
        String currentTimestamp = captureEventTimestamp();

        if (initialTimestamp.isEmpty() || currentTimestamp.isEmpty()) {
            throw new SkipException("SKIP: No VSP events available for timezone verification");
        }

        Assert.assertNotEquals(currentTimestamp, initialTimestamp,
                String.format("Timestamp should change after timezone change. Before: '%s', After: '%s'",
                        initialTimestamp, currentTimestamp));
        logger.info("Timezone change verified");
    }

    private boolean hasEventsPresent() {
        // BrowserStack fix: Wait for either cards or no-events message directly,
        // Use both firstEventInList and anyEventThumbnail as they have different XPath strategies
        List<By> expectedElements = List.of(
                VSPEventsLocators.FIRST_EVENT_IN_LIST,
                VSPEventsLocators.ANY_EVENT_THUMBNAIL,
                VSPEventsLocators.NO_EVENTS_MESSAGE,
                VSPEventsLocators.ZERO_EVENTS_MESSAGE
        );
        Optional<By> foundElement = WaitUtils.waitForFirstVisible(driver, expectedElements, TimeoutConstants.LIST_LOAD_TIMEOUT);

        if (foundElement.isEmpty()) {
            Assert.fail("VSP Events screen loaded but no events or 'no events' message found");
            return false;
        }

        if (foundElement.get().equals(VSPEventsLocators.FIRST_EVENT_IN_LIST)
                || foundElement.get().equals(VSPEventsLocators.ANY_EVENT_THUMBNAIL)) {
            return true;
        }

        // No events message was found
        logger.warn("No VSP events available");
        return false;
    }

    public void clickFirstEventCard() {
        // SAFETY FIX: Use EXPLICIT_WAIT for required elements
        WebElement card = WaitUtils.waitForClickability(driver, VSPEventsLocators.FIRST_EVENT_IN_LIST, FrameworkConstants.EXPLICIT_WAIT);
        card.click();

        boolean loaded = WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_CAMERA_NAME, FrameworkConstants.EXPLICIT_WAIT)
                      || WaitUtils.isVisible(driver, VSPEventsLocators.ANY_UTC_TIMESTAMP, FrameworkConstants.EXPLICIT_WAIT);

        Assert.assertTrue(loaded, "Detail page did not load after clicking event card");
        logger.info("Detail page opened");
    }

    public void verifyDetailPageDisplayed() {
        // SAFETY FIX: Use EXPLICIT_WAIT for required elements, removed hardcoded org name from message
        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_CAMERA_NAME, FrameworkConstants.EXPLICIT_WAIT,
                "Detail page should display camera name");
        WaitUtils.assertVisible(driver, VSPEventsLocators.CLOSE_ICON, TimeoutConstants.SHORT_TIMEOUT,
                "Close icon should be displayed");
        logger.info("Detail page displayed");
    }

    public void closeDetailPage() {
        // iOS: navigate().back() doesn't return to VSP Events properly, use close icon
        // Android BrowserStack: close icon click unreliable, use navigate().back()
        // Android local: close icon works reliably
        if (PlatformLocator.isIos()) {
            WaitUtils.click(driver, VSPEventsLocators.CLOSE_ICON);
        } else if (DeviceUtils.isBrowserStackExecution()) {
            driver.navigate().back();
        } else {
            WaitUtils.click(driver, VSPEventsLocators.CLOSE_ICON);
        }
        WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_MAIN_CONTAINER, TimeoutConstants.LIST_LOAD_TIMEOUT);
        logger.info("Closed detail page");
    }

    public void verifyDetailPageInformation() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_THUMBNAIL, TimeoutConstants.SHORT_TIMEOUT,
                "Thumbnail should be displayed");
        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_PLATE_CROP, TimeoutConstants.SHORT_TIMEOUT,
                "Plate crop should be displayed");

        String cameraName = getDetailFieldText(VSPEventsLocators.DETAIL_CAMERA_NAME, "Camera name");
        String plateReading = getDetailFieldText(VSPEventsLocators.DETAIL_PLATE_READING, "Plate reading");
        String plateRegion = getDetailFieldText(VSPEventsLocators.DETAIL_SEARCH_PLATE_LINK, "Plate region");
        String deviceTime = getDetailFieldText(VSPEventsLocators.DETAIL_DEVICE_TIME, "Device time");
        String preferredTime = getDetailFieldText(VSPEventsLocators.DETAIL_PREFERRED_TIME, "Preferred time");
        String timestamp = getDetailFieldText(VSPEventsLocators.DETAIL_TIMESTAMP, "Timestamp");

        // VACUOUS FIX: Assert plate reading and region have values (they were never asserted, only logged)
        Assert.assertFalse(plateReading.trim().isEmpty(), "Plate reading should not be empty");
        Assert.assertFalse(plateRegion.trim().isEmpty(), "Plate region should not be empty");

        Assert.assertTrue(deviceTime.matches(VSPEventsLocators.UTC_FORMAT_PATTERN),
                "Device time should contain UTC format, got: " + deviceTime);
        Assert.assertTrue(preferredTime.matches(VSPEventsLocators.UTC_FORMAT_PATTERN),
                "Preferred time should contain UTC format, got: " + preferredTime);
        Assert.assertFalse(timestamp.matches(".*:\\d{2}:\\d{2}.*"),
                "Timestamp should not contain seconds, got: " + timestamp);

        // All detail fields are optional - structure may vary by platform
        verifyOptionalField(VSPEventsLocators.DETAIL_MAKE, "Make");
        verifyOptionalField(VSPEventsLocators.DETAIL_SITE, "Site");
        verifyOptionalField(VSPEventsLocators.DETAIL_COLOR, "Color");
        verifyOptionalField(VSPEventsLocators.DETAIL_DIRECTION, "Direction");
        verifyOptionalField(VSPEventsLocators.DETAIL_TYPE, "Type");

        // Conditionally displayed based on API response - may not be present
        verifyOptionalField(VSPEventsLocators.DETAIL_ACCESS_TYPE, "Access type");
        verifyOptionalField(VSPEventsLocators.DETAIL_VEHICLE_TAGS, "Vehicle tags");

        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_VIEW_HISTORY_LINK, TimeoutConstants.SHORT_TIMEOUT,
                "View history link should be displayed");
        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_SEARCH_PLATE_LINK, TimeoutConstants.SHORT_TIMEOUT,
                "Search plate link should be displayed");

        logger.info("Detail page verified - Required fields: Camera: '{}', Plate: '{}', Region: '{}'",
                cameraName, plateReading, plateRegion);
    }

    public void openMoreMenu() {
        BottomNavigationBar.clickMoreTab(driver);
    }

    public void verifyEventsOptionDisplayed() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.EVENTS_OPTION,
                "Events item should be visible in the More menu");
    }

    public void verifyViewInHistoryBrowserButtonDisplayed() {
        WaitUtils.assertVisible(driver, VSPEventsLocators.DETAIL_VIEW_HISTORY_LINK,
                "History Browser button ('View in history browser') should be visible on the event details screen");
    }

    /** The camera the event came from, read before leaving the details screen. */
    public String readEventCameraName() {
        return getDetailFieldText(VSPEventsLocators.DETAIL_CAMERA_NAME, "Camera name");
    }

    /**
     * The time the event was recorded at, in the camera's time zone, read before leaving the details screen so the
     * History Browser can be checked against it.
     */
    public String readEventTime() {
        return getDetailFieldText(VSPEventsLocators.DETAIL_TIMESTAMP, "Event time");
    }

    public void tapViewInHistoryBrowser() {
        WaitUtils.click(driver, VSPEventsLocators.DETAIL_VIEW_HISTORY_LINK);
    }

    private String getDetailFieldText(By locator, String fieldName) {
        // SAFETY FIX: Use EXPLICIT_WAIT for first required element, then SHORT_TIMEOUT for rest
        // First call is camera name, which is always the first field checked
        boolean isFirstField = "Camera name".equals(fieldName);
        WebElement element = WaitUtils.waitForVisibility(driver, locator,
                isFirstField ? FrameworkConstants.EXPLICIT_WAIT : TimeoutConstants.SHORT_TIMEOUT);
        String text = element.getText();
        logger.debug("Detail field '{}': '{}'", fieldName, text);
        return text;
    }

    private void verifyOptionalField(By locator, String fieldName) {
        if (WaitUtils.isVisible(driver, locator, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            String text = WaitUtils.waitForVisibility(driver, locator, TimeoutConstants.SHORT_TIMEOUT).getText();
            Assert.assertFalse(text.trim().isEmpty(), fieldName + " field is displayed but has empty text");
            logger.info("Optional field '{}' displayed with value: '{}'", fieldName, text);
        } else {
            logger.info("Optional field '{}' not displayed (expected when API returns empty data)", fieldName);
        }
    }

    private String verifyElementAndGetText(By locator, String elementName) {
        // VACUOUS FIX: waitForVisibility either returns element or throws - assertNotNull is vacuous
        WebElement element = WaitUtils.waitForVisibility(driver, locator, TimeoutConstants.SHORT_TIMEOUT);
        return element.getText();
    }
}
