package pages;

import io.appium.java_client.AppiumDriver;
import locators.BottomNavigationLocators;
import org.openqa.selenium.By;
import utils.WaitUtils;

/**
 * The tab bar at the bottom of the app, shown on every main screen. An end user sees Layouts, Tags, Search and More;
 * a reseller sees Dashboard, Users, Business Portal and More.
 */
public final class BottomNavigationBar {

    private BottomNavigationBar() {
    }

    // ---- end user tabs ---------------------------------------------------------------------

    public static void clickLayoutsTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.LAYOUTS_TAB);
    }

    public static void clickTagsTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.TAGS_TAB);
    }

    public static void clickSearchTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.SEARCH_TAB);
    }

    public static void verifyEndUserTabsDisplayed(AppiumDriver driver) {
        verifyTabDisplayed(driver, BottomNavigationLocators.LAYOUTS_TAB, "Layouts");
        verifyTabDisplayed(driver, BottomNavigationLocators.TAGS_TAB, "Tags");
        verifyTabDisplayed(driver, BottomNavigationLocators.SEARCH_TAB, "Search");
        verifyTabDisplayed(driver, BottomNavigationLocators.MORE_TAB, "More");
    }

    // ---- reseller tabs ---------------------------------------------------------------------

    public static void clickDashboardTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.DASHBOARD_TAB);
    }

    public static void clickUsersTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.USERS_TAB);
    }

    public static void clickBusinessPortalTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.BUSINESS_PORTAL_TAB);
    }

    public static void verifyUsersTabDisplayed(AppiumDriver driver) {
        verifyTabDisplayed(driver, BottomNavigationLocators.USERS_TAB, "Users");
    }

    public static void verifyResellerTabsDisplayed(AppiumDriver driver) {
        verifyTabDisplayed(driver, BottomNavigationLocators.DASHBOARD_TAB, "Dashboard");
        verifyTabDisplayed(driver, BottomNavigationLocators.USERS_TAB, "Users");
        verifyTabDisplayed(driver, BottomNavigationLocators.BUSINESS_PORTAL_TAB, "Business Portal");
        verifyTabDisplayed(driver, BottomNavigationLocators.MORE_TAB, "More");
    }

    // ---- both ----------------------------------------------------------------------------------

    /** Taps the More tab, from any screen that shows the bottom navigation. */
    public static void clickMoreTab(AppiumDriver driver) {
        WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);
    }

    /** True when the tab bar is showing, judged by its More tab (instant check, no waiting). */
    public static boolean isShowing(AppiumDriver driver) {
        return WaitUtils.isPresent(driver, BottomNavigationLocators.MORE_TAB);
    }

    private static void verifyTabDisplayed(AppiumDriver driver, By tab, String tabName) {
        WaitUtils.assertVisible(driver, tab, "The " + tabName + " tab should be displayed in the bottom navigation");
    }
}
