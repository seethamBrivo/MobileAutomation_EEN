package pages.ios;

import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import java.util.Map;
import locators.ios.IosDevicesLocators;
import locators.ios.IosResellerDashboardLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.WaitUtils;

/** iOS counterpart of the reseller-dashboard checks in {@code pages.ResellerDashboardPage}. */
public class IosResellerDashboardPage {

    private static final Logger logger = LogManager.getLogger(IosResellerDashboardPage.class);
    private final AppiumDriver driver;

    public IosResellerDashboardPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public void verifyDashboardPageDisplayed() {
        Assert.assertTrue(WaitUtils.isVisible(driver, IosResellerDashboardLocators.DASHBOARD_TITLE,
                TimeoutConstants.DETAILS_LOAD_TIMEOUT), "Dashboard page should be visible after reseller login");
    }

    public void verifyDashboardSummary() {
        assertShown("Total accounts with count", IosResellerDashboardLocators.TOTAL_ACCOUNTS);
    }

    /** The account's card: large icon, name, bridge and camera icons and the four camera status dots. */
    public void verifyAccountCardContents(String accountName) {
        assertShown("Large model transformation icon", IosResellerDashboardLocators.LARGE_CARD_ICON);
        assertShown("Account name " + accountName, IosResellerDashboardLocators.accountName(accountName));
        assertShown("Bridge resource icon", IosResellerDashboardLocators.BRIDGE_ICON);
        assertShown("Camera resource icon", IosResellerDashboardLocators.CAMERA_ICON);
        String[] meanings = {"green (online)", "red (offline)", "yellow (warning)", "grey (other)"};
        for (int position = 1; position <= meanings.length; position++) {
            assertShown("Status dot " + position + ", " + meanings[position - 1],
                    IosResellerDashboardLocators.cameraStatusDot(accountName, position));
        }
    }

    /** Opens the first card's three-dots menu, checks its options and closes it. */
    public void verifyContextMenuOptions() {
        WaitUtils.click(driver, IosResellerDashboardLocators.THREE_DOTS_BUTTON);
        assertShown("Settings option", IosResellerDashboardLocators.CONTEXT_MENU_SETTINGS);
        assertShown("Delete account option", IosResellerDashboardLocators.CONTEXT_MENU_DELETE_ACCOUNT);
        dismissContextMenu();
    }

    /** Taps the dimmed area beside the menu, which closes it without choosing an option. */
    private void dismissContextMenu() {
        driver.executeScript("mobile: tap", Map.of("x", 12.0, "y", 440.0));
        Assert.assertTrue(WaitUtils.waitForInvisibility(driver, IosResellerDashboardLocators.CONTEXT_MENU_SETTINGS,
                TimeoutConstants.SHORT_TIMEOUT), "Context menu should close");
        logger.info("Context menu dismissed");
    }

    public void verifyDashboardToolbar() {
        assertShown("Filter by account name", IosResellerDashboardLocators.FILTER_BY_ACCOUNT);
        assertShown("Add account button", IosResellerDashboardLocators.ADD_ACCOUNT_BUTTON);
    }

    public void verifyBottomNavigationBar() {
        assertShown("Dashboard option", IosResellerDashboardLocators.NAV_DASHBOARD);
        assertShown("Users option", IosResellerDashboardLocators.NAV_USERS);
        assertShown("Business Portal option", IosResellerDashboardLocators.NAV_BUSINESS_PORTAL);
        assertShown("More option", IosDevicesLocators.MORE_TAB);
    }

    private void assertShown(String what, By locator) {
        Assert.assertTrue(WaitUtils.isVisible(driver, locator, TimeoutConstants.DETAILS_LOAD_TIMEOUT),
                what + " should be displayed");
    }
}
