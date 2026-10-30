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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    /** The test case says the event time never carries milliseconds. */
    private static final Pattern EVENT_TIME_FORMAT = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

    private static final Pattern EVENT_COUNTER_FORMAT = Pattern.compile("\\d+/\\d+");

    private static final Logger logger = LogManager.getLogger(VideoSearchPage.class);
    private final AppiumDriver driver;

    /** What the event page showed, so the history browser can be compared with it. */
    public record OpenedEvent(String cameraName, LocalDateTime dateTime) {}

    public VideoSearchPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void navigateBackFromVSPEvents() {
        WaitUtils.click(driver, VideoSearchLocators.BACK_BUTTON_FROM_VSP_EVENTS);
    }

    public void clickSearch() {
        WaitUtils.click(driver, VideoSearchLocators.SEARCH_OPTION);
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
            Assert.assertFalse(
                    WaitUtils.isVisible(driver, VideoSearchLocators.PEOPLE_LOADING, TimeoutConstants.SHORT_TIMEOUT),
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
        // Only the shown key image has a time. Reading one of several would compare the wrong moment.
        List<WebElement> times = WaitUtils.findElements(driver, VideoSearchLocators.EVENT_TIME);
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
}
