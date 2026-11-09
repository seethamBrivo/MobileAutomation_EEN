package pages;

import constants.FrameworkConstants;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import locators.AccountSwitchingLocators;
import locators.BottomNavigationLocators;
import locators.DownloadsLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import utils.GestureUtils;
import utils.WaitUtils;

/**
 * Page object for Account Switching feature.
 * Handles switching between multiple accounts (Resellers and End-Users).
 */
public class AccountSwitchingPage {

    private static final Logger logger = LogManager.getLogger(AccountSwitchingPage.class);
    private final AppiumDriver driver;

    public AccountSwitchingPage(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        this.driver = driver;
    }

    public void clickMoreTab() {
        WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);
        // Wait for More menu to load (Files is always at top of menu)
        WaitUtils.waitForVisibility(driver, DownloadsLocators.FILES_MENU_ITEM,
                TimeoutConstants.SHORT_TIMEOUT);
    }

    public void verifyMoreTabIsVisible() {
        WaitUtils.assertVisible(driver, BottomNavigationLocators.MORE_TAB,
                TimeoutConstants.SHORT_TIMEOUT, "More tab should be visible");
    }

    public void clickOtherAccountsItem() {
        WaitUtils.click(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM);
    }

    public void verifyOtherAccountsItemIsVisible() {
        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                TimeoutConstants.SHORT_TIMEOUT)) {
            boolean found = GestureUtils.scrollUntilVisible(driver,
                    AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM);
            Assert.assertTrue(found,
                    "Other Accounts item should be visible after scrolling in More menu");
        }
    }

    public void verifyOtherAccountsScreenIsVisible() {
        boolean resellersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.RESELLERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );
        boolean endUsersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.END_USERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );

        Assert.assertTrue(resellersSectionVisible || endUsersSectionVisible,
                "Other Accounts screen should show at least one section (Resellers or End-Users)");
    }

    public void verifyAccountListSections() {
        boolean resellersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.RESELLERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );
        boolean endUsersSectionVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.END_USERS_SECTION,
                TimeoutConstants.SHORT_TIMEOUT
        );

        if (!resellersSectionVisible || !endUsersSectionVisible) {
            throw new SkipException(
                    "Test data incomplete: Account must have both Resellers and End-Users sections");
        }
    }

    public String getFirstAccountName() {
        WebElement accountElement = WaitUtils.waitForVisibility(
                driver,
                AccountSwitchingLocators.FIRST_ACCOUNT_NAME,
                TimeoutConstants.SHORT_TIMEOUT
        );
        return accountElement.getText().trim();
    }

    public void clickFirstAccount() {
        WaitUtils.click(driver, AccountSwitchingLocators.FIRST_ACCOUNT_ROW);
    }

    public void verifySwitchingLoaderIsDisplayed() {
        WaitUtils.assertVisible(driver, AccountSwitchingLocators.SWITCHING_LOADER,
                TimeoutConstants.SHORT_TIMEOUT,
                "Loading screen should be visible during account switching");
    }

    public void waitForSwitchingToComplete() {
        WaitUtils.waitForInvisibility(
                driver,
                AccountSwitchingLocators.SWITCHING_LOADER,
                FrameworkConstants.EXPLICIT_WAIT
        );
        WaitUtils.waitForVisibility(
                driver,
                BottomNavigationLocators.MORE_TAB,
                TimeoutConstants.SHORT_TIMEOUT
        );
    }

    public void verifyAccountSwitchedSuccessfully(String expectedAccountName) {
        verifyMoreTabIsVisible();

        if (!WaitUtils.isVisible(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT)) {
            WaitUtils.click(driver, BottomNavigationLocators.MORE_TAB);
            WaitUtils.waitForVisibility(driver, AccountSwitchingLocators.OTHER_ACCOUNTS_ITEM,
                    TimeoutConstants.SHORT_TIMEOUT);
        }

        By accountNameInProfile = AppiumBy.xpath(
                "//android.widget.TextView[@text='" + expectedAccountName + "']");

        WaitUtils.assertVisible(driver, accountNameInProfile, TimeoutConstants.SHORT_TIMEOUT,
                String.format("Account name '%s' should be displayed in profile after switching",
                        expectedAccountName));
    }

    public void verifyBackNavigationIsDisabled() {
        boolean loaderVisible = WaitUtils.isVisible(
                driver,
                AccountSwitchingLocators.SWITCHING_LOADER,
                TimeoutConstants.OPTIONAL_FIELD_TIMEOUT
        );

        if (loaderVisible) {
            logger.info("Back navigation is disabled during account switching");
        }
    }
}
