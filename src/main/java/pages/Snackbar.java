package pages;

import io.appium.java_client.AppiumDriver;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import locators.CommonLocators;
import org.testng.Assert;
import utils.WaitUtils;

/** The short message that slides in at the bottom of the screen after an action (Android only; iOS shows none). */
public final class Snackbar {

    private Snackbar() {
    }

    /**
     * Waits for a snackbar and fails unless its text contains at least one of the fragments (ignoring case).
     *
     * @return the snackbar text as shown, so the caller can tell which fragment matched
     */
    public static String verifyContainsAnyOf(AppiumDriver driver, Duration timeout, String... expectedFragments) {
        String shownText = WaitUtils.waitForVisibility(driver, CommonLocators.SNACKBAR_TEXT, timeout).getText();
        String lowerCaseText = shownText.toLowerCase(Locale.ROOT);
        boolean matches = Arrays.stream(expectedFragments)
                .anyMatch(fragment -> lowerCaseText.contains(fragment.toLowerCase(Locale.ROOT)));
        Assert.assertTrue(matches, "Snackbar should contain one of " + Arrays.toString(expectedFragments)
                + " but was: " + shownText);
        return shownText;
    }

    /** Waits until a snackbar shows exactly {@code expectedText}, for steps whose message appears only at the end. */
    public static void waitUntilSays(AppiumDriver driver, String expectedText, Duration timeout) {
        String lastSeenText = WaitUtils.pollText(driver, CommonLocators.SNACKBAR_TEXT, expectedText, timeout);
        Assert.assertEquals(lastSeenText, expectedText, "Snackbar should show '" + expectedText + "'");
    }
}
