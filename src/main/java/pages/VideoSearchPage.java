package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import locators.VideoSearchLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.GestureUtils;
import utils.KeyboardUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

public class VideoSearchPage {

    private final AppiumDriver driver;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    /** Android spells the month out ("November 5, 2026"), iOS shortens it ("Nov 05, 2026"). */
    private static final DateTimeFormatter PLATFORM_EVENT_DATE_FORMAT =
            PlatformLocator.isIos() ? DATE_FORMAT : EVENT_DATE_FORMAT;

    private static final DateTimeFormatter TIME_12_HOUR =
            DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);

    /** The test case says the event time never carries milliseconds. Accepts both 24-hour and 12-hour AM/PM formats. */
    private static final Pattern EVENT_TIME_FORMAT = Pattern.compile("\\d{2}:\\d{2}:\\d{2}( (?i)[ap]m)?");

    /** Accepts both 24-hour (HH:mm:ss) and 12-hour (hh:mm:ss am/pm) formats. */
    private static LocalTime parseTime(String timeString) {
        String trimmed = timeString.trim();
        try {
            return LocalTime.parse(trimmed);
        } catch (DateTimeParseException e) {
            try {
                return LocalTime.parse(trimmed.toUpperCase(Locale.ROOT), TIME_12_HOUR);
            } catch (DateTimeParseException ex) {
                throw new AssertionError("Time '" + trimmed
                        + "' is not in a valid format (expected HH:mm:ss or hh:mm:ss AM/PM)", e);
            }
        }
    }

    /** Android reads "145/146"; iOS puts one space on each side of the slash. */
    private static final Pattern EVENT_COUNTER_FORMAT =
            Pattern.compile(PlatformLocator.isIos() ? "\\d+ / \\d+" : "\\d+/\\d+");

    /** "145/146" on Android, "48 / 48" on iOS. */
    private static final Pattern EVENT_POSITION = Pattern.compile("(\\d{1,6})\\s*/\\s*(\\d{1,6})");

    private static final long SECONDS_PER_DAY = 24 * 60 * 60;

    private static final Logger logger = LogManager.getLogger(VideoSearchPage.class);

    /** What the event page showed, so the history browser can be compared with it. */
    public record OpenedEvent(String cameraName, LocalDateTime dateTime) {}

    public VideoSearchPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromVSPEvents() {
        WaitUtils.click(driver, VideoSearchLocators.BACK_BUTTON_FROM_VSP_EVENTS);
    }

    public void verifyVideoSearchInBottomNav() {
        WaitUtils.assertVisible(driver, VideoSearchLocators.SEARCH_TAB, TimeoutConstants.SHORT_TIMEOUT,
                "Video Search should be visible in bottom navigation");
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
            eventDate = LocalDate.parse(date.trim(), PLATFORM_EVENT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new AssertionError("Event page date '" + date + "' is not in the expected '"
                    + (PlatformLocator.isIos() ? "MMM d, yyyy" : "MMMM d, yyyy") + "' format", e);
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
        return new OpenedEvent(cameraName, eventDate.atTime(parseTime(time)));
    }

    public void verifyVideoSearchPageDisplayed() {
        WaitUtils.assertVisible(driver, VideoSearchLocators.SEARCH_ICON, TimeoutConstants.SHORT_TIMEOUT,
                "Video Search page should be displayed");
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
        WaitUtils.assertVisible(driver, VideoSearchLocators.SEARCH_HINT_FIELD, TimeoutConstants.SHORT_TIMEOUT,
                "Search hint field should be visible");
    }

    public void verifyAutosuggestionOptions() {
        // Verify AI detection suggestions are available (keyboard must be closed to see all)
        List<String> suggestions = List.of("Person", "Man", "Woman", "Vehicle", "Backpack");
        for (String suggestion : suggestions) {
            WaitUtils.assertVisible(driver,
                    VideoSearchLocators.suggestionByText(suggestion),
                    TimeoutConstants.SHORT_TIMEOUT,
                    suggestion + " suggestion should be visible");
        }
    }

    private WebElement enterSearchText(String searchText) {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_HINT_FIELD);
        WebElement searchField = WaitUtils.waitForVisibility(driver, VideoSearchLocators.SEARCH_INPUT_FIELD);
        searchField.sendKeys(searchText);
        return searchField;
    }

    private void verifySearchTextEntered(WebElement searchField, String expectedText) {
        String actualText = searchField.getText();
        Assert.assertEquals(actualText, expectedText,
                "Search text should be '" + expectedText + "' but was '" + actualText + "'");
    }

    private void submitSearch() {
        if (PlatformLocator.isIos()) {
            WaitUtils.assertVisible(driver, VideoSearchLocators.KEYBOARD_SEARCH_KEY, TimeoutConstants.SHORT_TIMEOUT,
                    "The keyboard should be open to submit the search");
            WaitUtils.click(driver, VideoSearchLocators.KEYBOARD_SEARCH_KEY);
        } else {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.ENTER));
        }
    }

    private void verifySearchModeActive() {
        WaitUtils.assertVisible(driver, VideoSearchLocators.SEARCH_BACK_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT,
                "Search mode should be active (back button visible)");
    }

    private void verifyAppliedSearchTerm(String searchText) {
        WaitUtils.assertVisible(driver, VideoSearchLocators.appliedSearchTerm(searchText),
                TimeoutConstants.LIST_LOAD_TIMEOUT,
                "The results should show '" + searchText + "' as the applied search term");
    }

    private void clickSearchBackButton() {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_BACK_BUTTON);
    }

    private void verifyResultsShown(String searchText) {
        WaitUtils.assertVisible(driver, VideoSearchLocators.RESULTS_PREVIOUS_DAY_BUTTON,
                TimeoutConstants.LIST_LOAD_TIMEOUT, "The results screen should show its day navigation");
        WaitUtils.assertVisible(driver, VideoSearchLocators.FIRST_RESULT_CARD, TimeoutConstants.LIST_LOAD_TIMEOUT,
                "The results for '" + searchText + "' should list at least one event");
    }

    /** Verifies either results are shown OR empty state is displayed (when no results exist). */
    private void verifyResultsOrEmptyState(String searchText) {
        // Wait for day navigation to appear (present in both results and empty state)
        WaitUtils.assertVisible(driver, VideoSearchLocators.RESULTS_PREVIOUS_DAY_BUTTON,
                TimeoutConstants.LIST_LOAD_TIMEOUT, "The results screen should show its day navigation");

        // Check if results exist - if not, that's okay (empty state)
        boolean hasResults = WaitUtils.isVisible(driver, VideoSearchLocators.FIRST_RESULT_CARD,
                TimeoutConstants.LIST_LOAD_TIMEOUT);

        logger.info("Search for '{}' returned {} results", searchText, hasResults ? "some" : "no");
    }

    /** Run with the suggestions on screen, so an absent phrase is not just a list that has not loaded yet. */
    public void verifyRecentSearchNotListed(String phrase) {
        WaitUtils.assertNotVisible(driver, VideoSearchLocators.recentSearch(phrase), TimeoutConstants.SHORT_TIMEOUT,
                "'" + phrase + "' should not be a recent search before it has been searched");
    }

    public void verifyRecentSearchListed(String phrase) {
        WaitUtils.assertVisible(driver, VideoSearchLocators.recentSearch(phrase), TimeoutConstants.SHORT_TIMEOUT,
                "'" + phrase + "' should be listed as a recent search");
    }

    private void verifySearchFieldCancelled() {
        WaitUtils.assertNotVisible(driver, VideoSearchLocators.SEARCH_HINT_FIELD, TimeoutConstants.SHORT_TIMEOUT,
                "Search field should be closed after clicking back");
        WaitUtils.assertNotVisible(driver, VideoSearchLocators.SEARCH_BACK_BUTTON, TimeoutConstants.SHORT_TIMEOUT,
                "Search mode should be left after clicking back");
        WaitUtils.assertVisible(driver, VideoSearchLocators.LATEST_EVENTS_HEADLINE, TimeoutConstants.SHORT_TIMEOUT,
                "The Video Search home should show again after the search is cancelled");
    }

    public void navigateToVideoSearch() {
        clickSearch();
        verifyVideoSearchPageDisplayed();
    }

    public void openSearchField() {
        clickSearchIcon();
        verifySearchFieldOpened();
    }

    public void openSearchFieldAndVerifyAutosuggestions() {
        openSearchField();
        // Close keyboard to see all autosuggestions
        if (driver instanceof AndroidDriver) {
            try {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
                // Wait briefly for keyboard to close
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                logger.debug("Could not close keyboard: {}", e.getMessage());
            }
        } else {
            // iOS: the screen has no id to tap on, so close the keyboard the way the other pages do
            KeyboardUtils.hideKeyboard(driver);
        }
        verifyAutosuggestionOptions();
    }

    /** A suggestion such as "Person" is applied as a tag chip and is not kept as a recent search. */
    public void searchForText(String searchText) {
        WebElement searchField = enterSearchText(searchText);
        verifySearchTextEntered(searchField, searchText);
        submitSearch();
        verifySearchModeActive();
        verifyAppliedSearchTerm(searchText);
        verifyResultsShown(searchText);
    }

    /** A free-text phrase: the chip shows the app's own reading of it, not the typed text, so only the results show. */
    public void searchForPhrase(String phrase) {
        WebElement searchField = enterSearchText(phrase);
        verifySearchTextEntered(searchField, phrase);
        submitSearch();
        verifySearchModeActive();
        verifyResultsOrEmptyState(phrase);
    }

    public void closeSearchField() {
        clickSearchBackButton();
        verifySearchFieldCancelled();
    }

    public void clickCameraPreview() {
        WaitUtils.click(driver, VideoSearchLocators.FIRST_EVENT_PREVIEW);
    }

    private void verifyKeyImageDetailScreen() {
        WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_TIME);

        WaitUtils.assertVisible(driver, VideoSearchLocators.EVENT_CAMERA_NAME, TimeoutConstants.SHORT_TIMEOUT,
                "Camera name should be visible");
        WaitUtils.assertVisible(driver, VideoSearchLocators.EVENT_DATE, TimeoutConstants.SHORT_TIMEOUT,
                "Date should be visible");
        WaitUtils.assertVisible(driver, VideoSearchLocators.EVENT_COUNTER, TimeoutConstants.SHORT_TIMEOUT,
                "Event number scope should be visible");
        WaitUtils.assertVisible(driver, VideoSearchLocators.EVENT_TAGS_TITLE, TimeoutConstants.SHORT_TIMEOUT,
                "Tags title should be visible");
        WaitUtils.assertVisible(driver,
                VideoSearchLocators.VIEW_IN_HISTORY_BROWSER_BUTTON,
                TimeoutConstants.SHORT_TIMEOUT,
                "View History Browser button should be visible");
    }

    private void verifyTimestampHasNoMilliseconds() {
        String timestamp = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_TIME).getText();
        // Accept both 24-hour (HH:mm:ss) and 12-hour (hh:mm:ss AM/PM or am/pm) formats
        boolean isValid = Pattern.matches("\\d{2}:\\d{2}:\\d{2}", timestamp) ||
                Pattern.matches("(?i)\\d{2}:\\d{2}:\\d{2} [ap]m", timestamp);
        Assert.assertTrue(isValid,
                "Timestamp should be HH:mm:ss or hh:mm:ss AM/PM format without milliseconds but was '"
                        + timestamp + "'");
    }

    private void selectFirstTag() {
        if (PlatformLocator.isIos()) {
            // The event was opened from the Person section, so it has a tag.
            WaitUtils.assertVisible(driver, VideoSearchLocators.EVENT_TAGS, TimeoutConstants.SHORT_TIMEOUT,
                    "The key image should show a relevant tag");
            WaitUtils.click(driver, VideoSearchLocators.EVENT_TAGS);
            WaitUtils.assertVisible(driver, VideoSearchLocators.SELECTED_TAG_SEARCH_BUTTON,
                    TimeoutConstants.SHORT_TIMEOUT, "A tapped tag should show its search button");
            return;
        }
        if (WaitUtils.isVisible(driver, VideoSearchLocators.EVENT_TAGS, TimeoutConstants.SHORT_TIMEOUT)) {
            WaitUtils.click(driver, VideoSearchLocators.EVENT_TAGS);
            logger.info("Tag clicked");
        } else {
            logger.info("No tags detected - AI did not detect Person/Vehicle in this frame");
        }
    }

    /**
     * iOS keeps the neighbouring key images in the hierarchy off-screen, so only the time inside the screen counts.
     * It looks the texts up directly: it runs inside a poll, and {@code WaitUtils.findElements} would wait the full
     * explicit wait and throw when there is nothing yet.
     */
    private Optional<String> findShownTime() {
        if (!PlatformLocator.isIos()) {
            return WaitUtils.isVisible(driver, VideoSearchLocators.EVENT_TIME, TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)
                    ? Optional.of(WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_TIME).getText())
                    : Optional.empty();
        }
        int width = driver.manage().window().getSize().width;
        List<String> times = driver.findElements(VideoSearchLocators.KEY_IMAGE_TEXTS).stream()
                .filter(text -> {
                    Rectangle rect = text.getRect();
                    int centreX = rect.getX() + rect.getWidth() / 2;
                    return centreX >= 0 && centreX < width;
                })
                .map(WebElement::getText)
                .filter(text -> EVENT_TIME_FORMAT.matcher(text).matches())
                .toList();
        return times.size() == 1 ? Optional.of(times.get(0)) : Optional.empty();
    }

    private String readShownTime() {
        AtomicReference<String> shown = new AtomicReference<>();
        boolean found = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT, () -> {
            Optional<String> time = findShownTime();
            time.ifPresent(shown::set);
            return time.isPresent();
        });
        Assert.assertTrue(found, "The key image should show one time of day");
        return shown.get();
    }

    /**
     * The key image swipe starts and ends 1/parts of the image's width from its edges: Android 1/4 (a half-width swipe),
     * iOS 1/20 (a 90% flick). The key image is almost the full screen width (440 points on the iPhone 17 Pro Max), so
     * the iOS start is about 22 points in, clear of the 20 point strip at the screen edge that iOS keeps for gestures.
     */
    private static final int ANDROID_KEY_IMAGE_SWIPE_PARTS = 4;
    private static final int IOS_KEY_IMAGE_SWIPE_PARTS = 20;

    private record EventPosition(int current, int total) {}

    private EventPosition readEventPosition() {
        String counter = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_COUNTER).getText();
        Matcher matcher = EVENT_POSITION.matcher(counter);
        Assert.assertTrue(matcher.matches(), "Event counter should read 'current/total' but was '" + counter + "'");
        return new EventPosition(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    private String swipeKeyImageAndVerify(boolean towardsLeft, EventPosition before, String timeBefore,
            String cameraName) {
        String direction = towardsLeft ? "left" : "right";
        int expectedCurrent = before.current() + (towardsLeft ? 1 : -1);

        WebElement keyImage = WaitUtils.waitForVisibility(driver, VideoSearchLocators.KEY_IMAGE_OVERLAY);
        // Across the middle of the key image. iOS pages only when the drag covers most of the width (a half-width
        // drag left the counter unchanged on the iPhone 17 Pro Max while a 90% flick moved it), Android pages with half.
        Rectangle rect = keyImage.getRect();
        int parts = PlatformLocator.isIos() ? IOS_KEY_IMAGE_SWIPE_PARTS : ANDROID_KEY_IMAGE_SWIPE_PARTS;
        int nearEdge = rect.getWidth() / parts;
        int farEdge = rect.getWidth() * (parts - 1) / parts;
        int startX = rect.getX() + (towardsLeft ? farEdge : nearEdge);
        int endX = rect.getX() + (towardsLeft ? nearEdge : farEdge);
        int y = rect.getY() + (rect.getHeight() / 2);
        GestureUtils.swipe(driver, startX, y, endX, y);

        boolean moved = WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT,
                () -> readEventPosition().current() == expectedCurrent);
        Assert.assertTrue(moved, "Counter should read " + expectedCurrent + " of " + before.total()
                + " after swiping " + direction + " (was " + before.current() + ")");
        Assert.assertEquals(WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_CAMERA_NAME).getText(),
                cameraName, "Swiping " + direction + " should stay on the same camera");
        // Key images can be less than a second apart, so the time may stay the same but must not run backwards. The
        // difference wraps at midnight, so an event that spans it (23:59:58 then 00:00:02) still counts as later.
        String timeAfter = readShownTime();
        long seconds = Duration.between(parseTime(timeBefore), parseTime(timeAfter)).getSeconds();
        if (seconds > SECONDS_PER_DAY / 2) {
            seconds -= SECONDS_PER_DAY;
        } else if (seconds < -SECONDS_PER_DAY / 2) {
            seconds += SECONDS_PER_DAY;
        }
        Assert.assertTrue(towardsLeft ? seconds >= 0 : seconds <= 0,
                "Swiping " + direction + " should show a " + (towardsLeft ? "later" : "earlier")
                        + " (or equal) time than " + timeBefore + " but showed " + timeAfter);
        logger.info("Swiped {} on key image - {} of {} at {}, was {} at {}", direction, expectedCurrent,
                before.total(), timeAfter, before.current(), timeBefore);
        return timeAfter;
    }

    private void clickCloseButton() {
        WaitUtils.click(driver, VideoSearchLocators.CLOSE_BUTTON);
    }

    private void verifyDetailScreenDismissed() {
        WaitUtils.assertNotVisible(driver, VideoSearchLocators.EVENT_CAMERA_NAME, TimeoutConstants.SHORT_TIMEOUT,
                "Detail screen should be dismissed");
        WaitUtils.assertVisible(driver, VideoSearchLocators.LATEST_EVENTS_HEADLINE, TimeoutConstants.SHORT_TIMEOUT,
                "The Video Search home should show again after the detail screen is dismissed");
    }

    private void swipeDownToClose() {
        WebElement titleElement = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_CAMERA_NAME);
        Rectangle rect = titleElement.getRect();
        int centerX = rect.getX() + (rect.getWidth() / 2);
        int startY = rect.getY() + (rect.getHeight() / 2);
        Dimension windowSize = driver.manage().window().getSize();
        int endY = (int) (windowSize.getHeight() * 0.8); // Swipe down to 80% of screen
        GestureUtils.swipe(driver, centerX, startY, centerX, endY);
    }

    public void openCameraPreviewAndVerifyDetails() {
        clickCameraPreview();
        verifyKeyImageDetailScreen();
        verifyTimestampHasNoMilliseconds();
        selectFirstTag();
    }

    /**
     * The event opens on any position, often the last. The first swipe goes to the previous key image whenever there
     * is one, because the newest images can still be arriving on a live camera and move the counter while the swipe
     * runs; it goes to the next one only when the event opens on its first image.
     */
    public void verifySwipeNavigation() {
        EventPosition start = readEventPosition();
        if (start.total() < 2) {
            throw new SkipException("The event has one key image only, so there is no neighbour to swipe to");
        }
        String cameraName = WaitUtils.waitForVisibility(driver, VideoSearchLocators.EVENT_CAMERA_NAME).getText();
        String startTime = readShownTime();
        boolean towardsNext = start.current() == 1;
        String neighbourTime = swipeKeyImageAndVerify(towardsNext, start, startTime, cameraName);
        EventPosition neighbour = new EventPosition(start.current() + (towardsNext ? 1 : -1), start.total());
        swipeKeyImageAndVerify(!towardsNext, neighbour, neighbourTime, cameraName);
        Assert.assertTrue(WaitUtils.pollUntil(driver, TimeoutConstants.SHORT_TIMEOUT,
                        () -> findShownTime().map(startTime::equals).orElse(false)),
                "Swiping back should show the first key image again (time " + startTime + ")");
    }

    public void dismissDetailScreenWithXButton() {
        clickCloseButton();
        verifyDetailScreenDismissed();
    }

    public void dismissDetailScreenWithSwipeDown() {
        swipeDownToClose();
        verifyDetailScreenDismissed();
    }

    /**
     * Cleanup method called from @AfterMethod to dismiss any open screens.
     * Uses fast timeout to minimize overhead. Never throws.
     */
    public void dismissAnyOpenScreens() {
        if (WaitUtils.isVisible(driver, VideoSearchLocators.EVENT_CAMERA_NAME,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            clickCloseButton();
        }

        if (WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_BACK_BUTTON,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            clickSearchBackButton();
        }

        if (WaitUtils.isVisible(driver, VideoSearchLocators.SEARCH_HINT_FIELD,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            clickSearchBackButton();
        }
    }
}
