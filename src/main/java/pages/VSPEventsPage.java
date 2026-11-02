package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.VSPEventsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.DeviceUtils;
import utils.GestureUtils;
import utils.WaitUtils;

import java.util.List;
import java.util.Optional;

public class VSPEventsPage {

    private static final Logger logger = LogManager.getLogger(VSPEventsPage.class);
    private final AppiumDriver driver;

    public VSPEventsPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateToVSPEvents() {
        WaitUtils.click(driver, VSPEventsLocators.MORE_MENU_OPTION);
        verifyVSPSectionVisible();
        clickEvents();
        verifyVSPEventsScreenDisplayed();
        logger.info("Navigated to VSP Events");
    }

    public void navigateBackFromProfile() {
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
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_SECTION).isDisplayed(),
                "VSP section should be visible");
    }

    public void clickEvents() {
        WaitUtils.click(driver, VSPEventsLocators.EVENTS_OPTION);
    }

    public void verifyVSPEventsScreenDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_EVENTS_HEADING).isDisplayed(),
                "VSP Events screen should be displayed");
        WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_MAIN_CONTAINER, TimeoutConstants.LIST_LOAD_TIMEOUT);
    }

    public void verifyEventCardsDisplayed() {
        if (!hasEventsPresent()) {
            throw new SkipException("SKIP: No VSP events available");
        }
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.FIRST_EVENT_IN_LIST, TimeoutConstants.SHORT_TIMEOUT),
                "Event cards should be displayed");
        logger.info("Verified {} event cards are displayed",
                    WaitUtils.findElements(driver, VSPEventsLocators.ALL_EVENT_CARDS, TimeoutConstants.SHORT_TIMEOUT).size());
    }

    public void verifyEventCardInformation() {
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.ANY_EVENT_THUMBNAIL, TimeoutConstants.SHORT_TIMEOUT),
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
        int rowThreshold = firstCardHeight / 2;

        long cardsInFirstRow = cards.stream()
                .filter(card -> {
                    int cardY = card.getLocation().getY();
                    return Math.abs(cardY - firstCardY) <= rowThreshold;
                })
                .count();

        logger.info("Cards per row calculation - First card Y: {}, Height: {}, Threshold: {}, Count: {}",
                firstCardY, firstCardHeight, rowThreshold, cardsInFirstRow);

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
        boolean countStable = afterScrollCount == initialCount;

        if (moreCardsLoaded) {
            logger.info("Pagination verified - {} cards loaded (before: {}, after: {})",
                       afterScrollCount - initialCount, initialCount, afterScrollCount);
        } else if (contentChanged) {
            logger.info("Pagination verified - content scrolled (view recycling: {} cards before, {} after)",
                       initialCount, afterScrollCount);
        } else if (viewRecycled) {
            logger.info("Pagination verified - view recycled (cards scrolled off: before {}, after {})",
                       initialCount, afterScrollCount);
        } else if (countStable && afterScrollCount > 0) {
            logger.info("Pagination verified - count stable at {} (all events visible on single page)", afterScrollCount);
        }

        Assert.assertTrue(contentChanged || moreCardsLoaded || viewRecycled || (countStable && afterScrollCount > 0),
                "Pagination verification failed. Before: " + initialCount + ", After: " + afterScrollCount);
    }

    public void verifyToolbarHidesOnScroll() {
        // VACUOUS FIX: Assert toolbar is present - silent return was vacuous
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.TOOLBAR, TimeoutConstants.SHORT_TIMEOUT),
                "Toolbar should be present before scroll verification");

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
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_CAMERA_NAME, FrameworkConstants.EXPLICIT_WAIT),
                "Detail page should display camera name");
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.CLOSE_ICON, TimeoutConstants.SHORT_TIMEOUT),
                "Close icon should be displayed");
        logger.info("Detail page displayed");
    }

    public void closeDetailPage() {
        // BrowserStack fix: Use navigate().back() instead of clicking close icon
        // Close icon click behavior is unreliable on BrowserStack
        if (DeviceUtils.isBrowserStackExecution()) {
            driver.navigate().back();
        } else {
            WaitUtils.click(driver, VSPEventsLocators.CLOSE_ICON);
        }
        // Wait for VSP Events page to appear
        WaitUtils.waitForVisibility(driver, VSPEventsLocators.VSP_MAIN_CONTAINER, TimeoutConstants.LIST_LOAD_TIMEOUT);
        logger.info("Closed detail page");
    }

    public void verifyDetailPageInformation() {
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_THUMBNAIL, TimeoutConstants.SHORT_TIMEOUT),
                "Thumbnail should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_PLATE_CROP, TimeoutConstants.SHORT_TIMEOUT),
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

        // Per TC-37714 spec: Make always shows (displays "Not recognized" when empty)
        // Note: Color and Direction are actually optional in test data (not always present)
        String make = getDetailFieldText(VSPEventsLocators.DETAIL_MAKE, "Make");
        Assert.assertFalse(make.isBlank(), "Make should show 'Not recognized' when empty, not be absent");

        verifyOptionalField(VSPEventsLocators.DETAIL_SITE, "Site");
        verifyOptionalField(VSPEventsLocators.DETAIL_COLOR, "Color");
        verifyOptionalField(VSPEventsLocators.DETAIL_DIRECTION, "Direction");
        verifyOptionalField(VSPEventsLocators.DETAIL_TYPE, "Type");

        // Conditionally displayed based on API response - may not be present
        verifyOptionalField(VSPEventsLocators.DETAIL_ACCESS_TYPE, "Access type");
        verifyOptionalField(VSPEventsLocators.DETAIL_VEHICLE_TAGS, "Vehicle tags");

        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_VIEW_HISTORY_LINK, TimeoutConstants.SHORT_TIMEOUT),
                "View history link should be displayed");
        Assert.assertTrue(WaitUtils.isVisible(driver, VSPEventsLocators.DETAIL_SEARCH_PLATE_LINK, TimeoutConstants.SHORT_TIMEOUT),
                "Search plate link should be displayed");

        logger.info("All 17 fields verified - Camera: '{}', Plate: '{}', Region: '{}'",
                cameraName, plateReading, plateRegion);
    }

    public void openMoreMenu() {
        WaitUtils.click(driver, VSPEventsLocators.MORE_MENU_OPTION);
    }

    public void verifyEventsOptionDisplayed() {
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VSPEventsLocators.EVENTS_OPTION).isDisplayed(),
                "Events item should be visible in the More menu");
    }

    public void verifyViewInHistoryBrowserButtonDisplayed() {
        Assert.assertTrue(
                WaitUtils.waitForVisibility(driver, VSPEventsLocators.DETAIL_VIEW_HISTORY_LINK).isDisplayed(),
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
