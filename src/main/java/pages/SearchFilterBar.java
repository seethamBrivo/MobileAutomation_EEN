package pages;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.List;
import locators.BridgesLocators;
import utils.PlatformLocator;
import utils.WaitUtils;

/** The list filter that several screens open on iOS and that has to be closed before going back. */
public final class SearchFilterBar {

    private SearchFilterBar() {}

    /**
     * iOS: closes the filter field if it is open. A screen's Back button is hidden while the filter is open, so Back
     * has to wait until this has run. Android has no such button, so nothing happens there.
     */
    public static void closeIfOpenOnIos(AppiumDriver driver) {
        if (PlatformLocator.isIos()) {
            WaitUtils.tryClickOptionalElement(
                    driver, List.of(BridgesLocators.CLOSE_FILTER_BUTTON), TimeoutConstants.SHORT_TIMEOUT);
        }
    }
}
