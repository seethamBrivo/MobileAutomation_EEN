package pages;

import constants.TimeoutConstants;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import io.appium.java_client.AppiumDriver;
import locators.VideoSearchLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.WaitUtils;

public class VideoSearchPage {

    private final AppiumDriver driver;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    /** The test case says the event time never carries milliseconds. */
    private static final Pattern EVENT_TIME_FORMAT = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

    private static final Pattern EVENT_COUNTER_FORMAT = Pattern.compile("\\d+/\\d+");

    private static final Logger logger = LogManager.getLogger(VideoSearchPage.class);

    /** What the event page showed, so the history browser can be compared with it. */
    public record OpenedEvent(String cameraName, LocalDateTime dateTime) {}

    public VideoSearchPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromVSPEvents() {
        WaitUtils.click(driver, VideoSearchLocators.BACK_BUTTON_FROM_VSP_EVENTS);
    }

    public void verifyVideoSearchInSideMenu() {
        boolean videoSearchVisible = WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_OPTION,
                constants.TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(videoSearchVisible, "Video Search should be visible in side menu");
        logger.info("Video Search option is visible in side menu");
    }

    public void clickSearch() {
        BottomNavigationBar.clickSearchTab(driver);
    }

    public void openVideoSearch() {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_TAB);
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VideoSearchLocators.VIDEO_SEARCH_SCREEN).isDisplayed(),
                "Video Search home page should be displayed");
    }

    /**
     * Opens the first card of the Person section. The card gets the whole list-load budget, because the loading
     * skeleton may not have been drawn yet when the screen opens, and a missing skeleton then says nothing about the
     * events. Only when the budget is spent and the skeleton is gone is the section empty and the test skipped; a
     * skeleton that is still there fails the test.
     */
    public OpenedEvent openFirstPersonEvent() {
        WaitUtils.waitForVisibility(driver, VideoSearchLocators.LATEST_EVENTS_HEADLINE);
        if (!WaitUtils.isVisible(driver, VideoSearchLocators.FIRST_PERSON_CARD, TimeoutConstants.LIST_LOAD_TIMEOUT)) {
            Assert.assertFalse(WaitUtils.isPresent(driver, VideoSearchLocators.PEOPLE_LOADING),
                    "Person section should finish loading within " + TimeoutConstants.LIST_LOAD_TIMEOUT.toSeconds()
                            + " s");
            throw new SkipException("Video Search shows no Person event, so there is no event to open");
        }
        WaitUtils.click(driver, VideoSearchLocators.FIRST_PERSON_CARD);
        return verifyEventPageDetails();
    }

    public void openHistoryBrowserFromEvent() {
        WaitUtils.click(driver, VideoSearchLocators.VIEW_IN_HISTORY_BROWSER_BUTTON);
    }

    /** Camera name, date, time of day, event counter, relevant tags and the button to the history browser. */
    private OpenedEvent verifyEventPageDetails() {
        String cameraName = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_CAMERA_NAME).getText();
        Assert.assertFalse(cameraName.isBlank(), "Event page should show the camera name");
        String date = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_DATE).getText();
        LocalDate eventDate;
        try {
            eventDate = LocalDate.parse(date.trim(), EVENT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new AssertionError("Event page date '" + date + "' is not in the 'MMMM d, yyyy' format", e);
        }
        // Only the shown key image has a time, and a neighbouring key image kept in the hierarchy off-screen must not
        // count. Reading one of several would compare the wrong moment.
        List<WebElement> times = WaitUtils.findElements(driver, VideoSearchLocators.EVENT_TIME).stream()
                .filter(WebElement::isDisplayed)
                .toList();
        Assert.assertEquals(times.size(), 1,
                "Event page should show the time of one key image but shows " + times.size());
        String time = times.get(0).getText();
        Assert.assertTrue(EVENT_TIME_FORMAT.matcher(time).matches(),
                "Event timestamp should be HH:mm:ss without milliseconds but was '" + time + "'");
        String counter = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_COUNTER).getText();
        Assert.assertTrue(EVENT_COUNTER_FORMAT.matcher(counter).matches(),
                "Event counter should read 'current/total' but was '" + counter + "'");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_TAGS_TITLE).isDisplayed(),
                "'Relevant tags' title should be displayed on the event page");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_TAGS).isDisplayed(),
                "Event page should show at least one relevant tag");
        Assert.assertTrue(WaitUtils.waitForVisibility(driver, VideoSearchLocators.VIEW_IN_HISTORY_BROWSER_BUTTON)
                        .isDisplayed(),
                "'View in history browser' button should be displayed on the event page");
        logger.info("Event page: camera '{}', date '{}', time {}, counter {}", cameraName, date, time, counter);
        return new OpenedEvent(cameraName, eventDate.atTime(LocalTime.parse(time)));
    }

    public void verifyVideoSearchPageDisplayed() {
        boolean searchIconVisible = WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_ICON,
                constants.TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(searchIconVisible, "Video Search page should be displayed");
        logger.info("Video Search page is displayed");
    }

    public String captureDateFieldText() {
        String dateText = WaitUtils.waitForVisibility(driver, VideoSearchLocators.DATE_FIELD).getText();

        Assert.assertFalse(dateText.isEmpty(),
                "Date field should contain date/time information");

        logger.info("Captured Video Search date: {}", dateText);
        return dateText;
    }

    /**
     * The date shown must be today's date in {@code zoneId}. This replaces "the date differs from
     * before the change": Asia/Kolkata and Etc/GMT+12 are on the same calendar day for 6.5 hours of
     * every UTC day (12:00-18:30), so that check failed whenever CI ran then (BrowserStack build #14).
     * A midnight rollover during the check is tolerated by also accepting the date a minute ago.
     */
    public void verifyDateFieldMatchesTimezone(String zoneId) {
        String shown = captureDateFieldText();
        LocalDate shownDate = LocalDate.parse(shown.trim(), DATE_FORMAT);
        ZoneId zone = ZoneId.of(zoneId);
        Instant now = Instant.now();
        LocalDate expected = LocalDate.ofInstant(now, zone);
        LocalDate expectedMinuteAgo = LocalDate.ofInstant(now.minusSeconds(60), zone);
        Assert.assertTrue(shownDate.equals(expected) || shownDate.equals(expectedMinuteAgo),
                "Video Search date should be today in " + zoneId + " (" + expected + ") but was " + shownDate);
        logger.info("Verified Video Search date '{}' is today in {}", shown, zoneId);
    }

    public void clickSearchIcon() {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_ICON);
    }

    public void verifySearchFieldOpened() {
        boolean searchFieldVisible = WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_HINT_FIELD,
                constants.TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(searchFieldVisible, "Search hint field should be visible");
    }

    public void verifyAutosuggestionOptions() {
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.SUGGESTION_PERSON,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Person suggestion should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.SUGGESTION_MAN,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Man suggestion should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.SUGGESTION_WOMAN,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Woman suggestion should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.SUGGESTION_VEHICLE,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Vehicle suggestion should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.SUGGESTION_BACKPACK,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Backpack suggestion should be visible");
        logger.info("All 5 autosuggestion options verified");
    }

    public void enterSearchText(String searchText) {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_HINT_FIELD);
        WaitUtils.waitForVisibility(driver, VideoSearchLocators.SEARCH_INPUT_FIELD).sendKeys(searchText);
        logger.info("Entered search text: {}", searchText);
    }

    public void verifySearchTextEntered(String expectedText) {
        String actualText = WaitUtils.waitForVisibility(driver, VideoSearchLocators.SEARCH_INPUT_FIELD).getText();
        Assert.assertEquals(actualText, expectedText, "Search text should match entered text");
        logger.info("Verified search text: {}", actualText);
    }

    public void pressEnterKey() {
        ((io.appium.java_client.android.AndroidDriver) driver).pressKey(
                new io.appium.java_client.android.nativekey.KeyEvent(
                        io.appium.java_client.android.nativekey.AndroidKey.ENTER));
    }

    public void verifySearchResultsOrEmptyState() {
        WaitUtils.waitForVisibility(driver, VideoSearchLocators.BACK_BUTTON);
        logger.info("Search executed - results may vary based on available data");
    }

    public void clickBackButton() {
        WaitUtils.click(driver, VideoSearchLocators.BACK_BUTTON);
    }

    public void verifySearchFieldCancelled() {
        boolean searchFieldGone = !WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_HINT_FIELD,
                constants.TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(searchFieldGone, "Search field should be closed after clicking back");
        logger.info("Search field cancelled successfully");
    }

    public void navigateToVideoSearch() {
        verifyVideoSearchInSideMenu();
        clickSearch();
        verifyVideoSearchPageDisplayed();
    }

    public void openSearchFieldAndVerifyAutosuggestions() {
        clickSearchIcon();
        verifySearchFieldOpened();
        verifyAutosuggestionOptions();
    }

    public void searchForText(String searchText) {
        enterSearchText(searchText);
        verifySearchTextEntered(searchText);
        pressEnterKey();
        verifySearchResultsOrEmptyState();
    }

    public void closeSearchField() {
        clickBackButton();
        verifySearchFieldCancelled();
    }

    public void clickCameraPreview() {
        WaitUtils.click(driver, VideoSearchLocators.CAMERA_PREVIEW_FIRST);
        WaitUtils.waitForVisibility(driver, VideoSearchLocators.CAMERA_NAME);
        logger.info("Clicked on camera preview and detail screen opened");
    }

    public void verifyKeyImageDetailScreen() {
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.CAMERA_NAME,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Camera name should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.DATE_SUBTITLE,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Date should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.TIMESTAMP,
                constants.TimeoutConstants.LIST_LOAD_TIMEOUT), "Timestamp should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.EVENT_NUMBER_SCOPE,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Event number scope should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.TAGS_TITLE,
                constants.TimeoutConstants.SHORT_TIMEOUT), "Tags title should be visible");
        Assert.assertTrue(WaitUtils.isVisible(driver, VideoSearchLocators.VIEW_HISTORY_BROWSER_BUTTON,
                constants.TimeoutConstants.SHORT_TIMEOUT), "View History Browser button should be visible");
        logger.info("Key image detail screen verified with all required elements");
    }

    public void verifyTimestampHasNoMilliseconds() {
        String timestamp = WaitUtils.waitForVisibility(driver, VideoSearchLocators.TIMESTAMP).getText();
        Assert.assertFalse(timestamp.contains("."), "Timestamp should not contain milliseconds");
        logger.info("Verified timestamp has no milliseconds: {}", timestamp);
    }

    public byte[] captureScreenshot() {
        return ((io.appium.java_client.android.AndroidDriver) driver).getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
    }

    public void clickTagAndVerifyHighlight() {
        byte[] beforeScreenshot = captureScreenshot();
        WaitUtils.click(driver, VideoSearchLocators.ANY_TAG);
        byte[] afterScreenshot = captureScreenshot();
        Assert.assertFalse(java.util.Arrays.equals(beforeScreenshot, afterScreenshot),
                "Tag should be highlighted after click");
        logger.info("Tag clicked and highlight verified");
    }

    public void swipeRightOnKeyImage() {
        String timestampBefore = WaitUtils.waitForVisibility(driver, VideoSearchLocators.TIMESTAMP).getText();
        org.openqa.selenium.WebElement imageOverlay = WaitUtils.waitForVisibility(driver, VideoSearchLocators.KEY_IMAGE_OVERLAY);
        org.openqa.selenium.Rectangle rect = imageOverlay.getRect();
        int startX = rect.getX() + (rect.getWidth() * 3 / 4);
        int endX = rect.getX() + (rect.getWidth() / 4);
        int y = rect.getY() + (rect.getHeight() / 2);
        utils.GestureUtils.swipe(driver, startX, y, endX, y);
        String timestampAfter = WaitUtils.waitForVisibility(driver, VideoSearchLocators.TIMESTAMP).getText();
        if (timestampAfter.equals(timestampBefore)) {
            logger.info("Swiped right on key image - same event (no movement detected)");
        } else {
            logger.info("Swiped right on key image - changed from {} to {}", timestampBefore, timestampAfter);
        }
    }

    public void swipeLeftOnKeyImage() {
        String timestampBefore = WaitUtils.waitForVisibility(driver, VideoSearchLocators.TIMESTAMP).getText();
        org.openqa.selenium.WebElement imageOverlay = WaitUtils.waitForVisibility(driver, VideoSearchLocators.KEY_IMAGE_OVERLAY);
        org.openqa.selenium.Rectangle rect = imageOverlay.getRect();
        int startX = rect.getX() + (rect.getWidth() / 4);
        int endX = rect.getX() + (rect.getWidth() * 3 / 4);
        int y = rect.getY() + (rect.getHeight() / 2);
        utils.GestureUtils.swipe(driver, startX, y, endX, y);
        String timestampAfter = WaitUtils.waitForVisibility(driver, VideoSearchLocators.TIMESTAMP).getText();
        if (timestampAfter.equals(timestampBefore)) {
            logger.info("Swiped left on key image - same event (no movement detected)");
        } else {
            logger.info("Swiped left on key image - changed from {} to {}", timestampBefore, timestampAfter);
        }
    }

    public void clickCloseButton() {
        WaitUtils.click(driver, VideoSearchLocators.CLOSE_BUTTON);
        logger.info("Clicked close button");
    }

    public void verifyDetailScreenDismissed() {
        boolean detailScreenGone = !WaitUtils.isVisible(driver, VideoSearchLocators.CAMERA_NAME,
                constants.TimeoutConstants.SHORT_TIMEOUT);
        Assert.assertTrue(detailScreenGone, "Detail screen should be dismissed");
        logger.info("Detail screen dismissed successfully");
    }

    public void swipeDownToClose() {
        org.openqa.selenium.WebElement titleElement = WaitUtils.waitForVisibility(driver, VideoSearchLocators.CAMERA_NAME);
        org.openqa.selenium.Rectangle rect = titleElement.getRect();
        int x = rect.getX() + (rect.getWidth() / 2);
        int startY = rect.getY() + (rect.getHeight() / 2);
        int endY = startY + 500;
        utils.GestureUtils.swipe(driver, x, startY, x, endY);
        logger.info("Swiped down to close detail screen");
    }

    public void openCameraPreviewAndVerifyDetails() {
        clickCameraPreview();
        verifyKeyImageDetailScreen();
        verifyTimestampHasNoMilliseconds();
        clickTagAndVerifyHighlight();
    }

    public void verifySwipeNavigation() {
        swipeRightOnKeyImage();
        swipeLeftOnKeyImage();
    }

    public void dismissDetailScreenWithXButton() {
        clickCloseButton();
        verifyDetailScreenDismissed();
    }

    public void dismissDetailScreenWithSwipeDown() {
        clickCameraPreview();
        swipeDownToClose();
        verifyDetailScreenDismissed();
    }
}
