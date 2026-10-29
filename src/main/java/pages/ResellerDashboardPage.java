package pages;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumDriver;
import locators.ResellerDashboardLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

import java.time.Duration;

public class ResellerDashboardPage {
    private final AppiumDriver driver;

    public ResellerDashboardPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardPageDisplayed() {
        // Right after login the dashboard can sit on skeleton rows for longer than the default wait.
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.RESELLER_DASHBOARD_TITLE, TimeoutConstants.LIST_LOAD_TIMEOUT).isDisplayed();
    }

    /** Step "Click an account that has a bridge": re-login into it and wait for its Layouts section. */
    public void openFirstAccountWithBridges() {
        clickFirstAccountWithBridges();
        verifyLayoutsScreenDisplayed();
    }

    public void clickFirstAccountWithBridges() {
        WaitUtils.waitForClickability(driver, ResellerDashboardLocators.FIRST_ACCOUNT_WITH_BRIDGES).click();
    }

    public boolean isLayoutsScreenDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.LAYOUTS_SCREEN).isDisplayed();
    }

    public boolean isTotalAccountsDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.TOTAL_ACCOUNTS).isDisplayed();
    }

    public boolean isLargeModelTransformationIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.TRANSFORMATION_ICON).isDisplayed();
    }

    public boolean isAccountNameDisplayed(String accountName) {
        return WaitUtils.waitForVisibility(
                driver,
                ResellerDashboardLocators.accountByName(accountName)
        ).isDisplayed();
    }

    public void scrollUntilAccountIsVisible(String accountName) {
        GestureUtils.scrollUntilPresent(driver, ResellerDashboardLocators.accountByName(accountName));
    }

    public boolean isBridgeResourceIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.BRIDGE_ICON).isDisplayed();
    }

    public boolean isGreenDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.DOT_GREEN).isDisplayed();
    }

    public boolean isRedDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.DOT_RED).isDisplayed();
    }

    public boolean isYellowDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.DOT_YELLOW).isDisplayed();
    }

    public boolean isGreyDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.DOT_GREY).isDisplayed();
    }

    public boolean isCameraResourceIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CAMERA_ICON).isDisplayed();
    }

    public void clickThreeDotsOption() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.THREE_DOTS_BUTTON).click();
    }

    public boolean isSettingsOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CONTEXT_MENU_SETTINGS).isDisplayed();
    }

    public boolean isDeleteAccountOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CONTEXT_MENU_DELETE_ACCOUNT).isDisplayed();
    }

    public boolean isFilterByAccountNameFilterDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.FILTER_BY_ACCOUNT_NAME).isDisplayed();
    }

    public boolean isAddAccountButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ADD_ACCOUNT_BUTTON).isDisplayed();
    }

    public boolean isDashboardOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.NAV_DASHBOARD).isDisplayed();
    }

    public boolean isUsersOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.NAV_USERS).isDisplayed();
    }

    public boolean isBusinessPortalOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.NAV_BUSINESS_PORTAL).isDisplayed();
    }

    public boolean isMoreOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.NAV_MORE).isDisplayed();
    }

    public void clickAddAccountButton() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ADD_ACCOUNT_BUTTON).click();
    }

    public boolean isAccountNameFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ACCOUNT_NAME_FIELD).isDisplayed();
    }

    public boolean isAddButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ADD_BUTTON).isDisplayed();
    }

    public boolean isAddCustomerIdFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CUSTOMER_ID_FIELD).isDisplayed();
    }

    public boolean isEmailFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EMAIL_FIELD).isDisplayed();
    }

    public boolean isEditionFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EDITION_FIELD).isDisplayed();
    }

    public boolean isStandardOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EDITION_STANDARD).isDisplayed();
    }

    public void clickEditionSectionNavigationIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EDITION_NAV_ICON).click();
    }

    public boolean isProfessionalOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EDITION_PROFESSIONAL).isDisplayed();
    }

    public boolean isEnterpriseOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EDITION_ENTERPRISE).isDisplayed();
    }

    public void clickEditionSectionBackNavigationIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.GENERIC_BACK_CHEVRON).click();
    }

    public boolean isCameraDefaultsSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CAMERA_DEFAULTS_SECTION).isDisplayed();
    }

    public boolean isCloudRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_RETENTION).isDisplayed();
    }

    public boolean isThirtyDaysDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_RETENTION_DEFAULT_30_DAYS).isDisplayed();
    }

    public void cloudRetentionSectionNavigationIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_RETENTION_NAV_ICON).click();
    }

    public boolean isMinimumOneDayOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_RETENTION_MIN_1_DAY).isDisplayed();
    }

    public boolean isMax5YearsOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_RETENTION_MAX_5_YEARS).isDisplayed();
    }

    public void clickCloudRetentionSectionBackNavigationIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.GENERIC_BACK_CHEVRON).click();
    }

    public boolean isMinRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MIN_RETENTION).isDisplayed();
    }

    public boolean is14DaysDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MIN_RETENTION_DEFAULT_14_DAYS).isDisplayed();
    }

    public void clickMinRetentionSectionViewIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MIN_RETENTION_NAV_ICON).click();
    }

    public boolean isMinRetentionSectionMinValueNoneDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MIN_RETENTION_MIN_NONE).isDisplayed();
    }

    public boolean isMinRetentionSectionMaxValue180DaysDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MIN_RETENTION_MAX_180_DAYS).isDisplayed();
    }

    public void clickMinRetentionSectionBackIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.GENERIC_BACK_CHEVRON).click();
    }

    public boolean isMaxRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MAX_RETENTION).isDisplayed();
    }

    public boolean sixtyDaysByDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MAX_RETENTION_DEFAULT_60_DAYS).isDisplayed();
    }

    public void clickMaxRetentionSectionViewIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MAX_RETENTION_NAV_ICON).click();
    }

    public boolean minimumValueOptionIsNoneInMaxRetentionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MAX_RETENTION_MIN_NONE).isDisplayed();
    }

    public boolean maxValueOptionIs1YearInMaxRetentionDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.MAX_RETENTION_MAX_1_YEAR).isDisplayed();
    }

    public void clickMaxRetentionSectionBackIcon() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.GENERIC_BACK_CHEVRON).click();
    }

    public boolean previewResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.PREVIEW_RESOLUTION).isDisplayed();
    }

    public boolean defaultpreviewResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.PREVIEW_RESOLUTION_DEFAULT).isDisplayed();
    }

    public boolean fullVideoResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.FULL_VIDEO_RESOLUTION).isDisplayed();
    }

    public boolean fullVideoResolutionDefaultValueIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.FULL_VIDEO_RESOLUTION_DEFAULT).isDisplayed();
    }

    public boolean cloudPreviewOnlyToggleButton() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CLOUD_PREVIEW_ONLY_TOGGLE).isDisplayed();
    }

    public void enterCustomerId(String customerID) {
        WebElement field = WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.CUSTOMER_ID_FIELD);
        field.clear();
        field.sendKeys(customerID);
    }

    public void enterAccountName(String accountName) {
        WebElement field = WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ACCOUNT_NAME_FIELD);
        field.clear();
        field.sendKeys(accountName);
    }

    public void clickAddOption() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ADD_BUTTON).click();
    }

    public boolean isMax120CharsErrorMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.ERROR_MAX_120_CHARS).isDisplayed();
    }

    public boolean isSuccessMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.SUCCESS_ADDED, Duration.ofSeconds(60)).isDisplayed();
    }

    public void clickEmailField() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EMAIL_FIELD).click();
    }

    public void enterEmail(String email) {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.EMAIL_INPUT_FIELD).sendKeys(email);
    }

    public void clickBackButton() {
        WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.GENERIC_BACK_CHEVRON).click();
    }

    // XPath 1.0 string escaping: uses concat() for values with quotes to prevent injection.
    // Example: John's "Test" → concat('John', "'", 's "Test"')
    private String buildXPathStringLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        String[] parts = value.split("'", -1);
        return "concat('" + String.join("',\"'\",'", parts) + "')";
    }

    public void longPressOnAccount(String accountNameWithCustomerId) {
        String xpathSafeText = buildXPathStringLiteral(accountNameWithCustomerId);
        By accountLocator = ResellerDashboardLocators.accountByExactText(xpathSafeText);
        WebElement accountElement = WaitUtils.waitForVisibility(driver, accountLocator);
        GestureUtils.longPress(driver, accountElement);
        // The press sometimes lands while the long list is still settling,press again, on a fresh lookup.
        for (int retry = 1; retry <= 2 && !WaitUtils.isVisible(driver, ResellerDashboardLocators.CONTEXT_MENU_DELETE_ACCOUNT, TimeoutConstants.SHORT_TIMEOUT); retry++) {
            GestureUtils.longPress(driver, WaitUtils.waitForVisibility(driver, accountLocator));
        }
    }

    /** Backs out of any add/edit screen so the account list (bottom navigation) is showing. */
    public void recoverToAccountList() {
        for (int i = 0; i < 3 && !WaitUtils.isPresent(driver, ResellerDashboardLocators.NAV_DASHBOARD); i++) {
            driver.navigate().back();
        }
    }

    public void clickDeleteAccountOption() {
        WaitUtils.waitForClickability(driver, ResellerDashboardLocators.CONTEXT_MENU_DELETE_ACCOUNT).click();
    }

    public void clickDeleteConfirmButton() {
        WaitUtils.waitForClickability(driver, ResellerDashboardLocators.DELETE_CONFIRM_BUTTON).click();
    }

    private static final java.util.regex.Pattern ACCOUNTS_COUNT_PATTERN = java.util.regex.Pattern.compile("^(\\d+) accounts?$");

    /** The number in the "N accounts" header, or -1 when it is not on screen. */
    public int getAccountCount() {
        for (WebElement label : driver.findElements(ResellerDashboardLocators.ACCOUNTS_COUNT_LABEL)) {
            java.util.regex.Matcher m = ACCOUNTS_COUNT_PATTERN.matcher(label.getText().trim());
            if (m.matches()) {
                return Integer.parseInt(m.group(1));
            }
        }
        return -1;
    }

    /**
     * After confirming a delete: passes when the "Deleted successfully" snackbar is seen OR the account
     * count in the list header drops below {@code countBefore}. The snackbar only stays for about 3
     * seconds and one lookup on the long account list can take that long, so waiting for the message
     * alone missed it (BrowserStack builds #23/#24, where the count did drop from 11 to 10).
     */
    public void verifyAccountDeleted(int countBefore) {
        boolean deleted;
        try {
            deleted = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(60))
                    .pollingEvery(Duration.ofMillis(250))
                    .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                    .until(d -> {
                        if (WaitUtils.isPresent(driver, ResellerDashboardLocators.DELETED_SUCCESS_MESSAGE)) {
                            return true;
                        }
                        if (countBefore < 0) {
                            return false;
                        }
                        int countNow = getAccountCount(); // one lookup per poll, not two
                        return countNow >= 0 && countNow < countBefore;
                    });
        } catch (org.openqa.selenium.TimeoutException e) {
            deleted = false;
        }
        Assert.assertTrue(deleted, "Account should be deleted: no 'Deleted successfully' message and the account count did not drop below " + countBefore);
    }

    /**
     * Logs out from wherever the test finished. Bottom navigation is reachable from most
     * screens; nested screens/dialogs are unwound with Back first. Throws if it cannot get
     * there so BaseTest can fall back to clearing the app.
     */
    public void logout() {
        // "More" is part of the bottom navigation, so its presence is all that matters. Give it a moment to show up
        // before pressing Back: right after a relaunch the screen may still be rendering, and Back from the landing
        // screen would leave the app. The wait only costs time when the screen really is somewhere else.
        for (int i = 0; i < 5 && !WaitUtils.isVisible(driver, ResellerDashboardLocators.NAV_MORE, TimeoutConstants.SHORT_TIMEOUT); i++) {
            driver.navigate().back();
        }
        WaitUtils.click(driver, ResellerDashboardLocators.NAV_MORE);
        if (!WaitUtils.isPresent(driver, ResellerDashboardLocators.LOGOUT_OPTION)) {
            GestureUtils.swipeUp(driver);
        }
        WaitUtils.click(driver, ResellerDashboardLocators.LOGOUT_OPTION);
        WaitUtils.click(driver, ResellerDashboardLocators.CONFIRM_LOGOUT_BUTTON);
    }

    public void verifyLayoutsScreenDisplayed() {
        Assert.assertTrue(isLayoutsScreenDisplayed(), "Layouts screen should be displayed");
    }

    public void verifyDashboardPageDisplayed() {
        Assert.assertTrue(isDashboardPageDisplayed(), "Dashboard page should be visible after reseller login");
    }

    public void verifyTotalAccountsDisplayed() {
        Assert.assertTrue(isTotalAccountsDisplayed(), "Total accounts with count should be visible");
    }

    public void verifyLargeModelTransformationIconDisplayed() {
        Assert.assertTrue(isLargeModelTransformationIconDisplayed(), "Large model transformation icon should be visible");
    }

    public void verifyAccountNameDisplayed(String accountName) {
        Assert.assertTrue(isAccountNameDisplayed(accountName), "Account name should be displayed: " + accountName);
    }

    public void verifyBridgeResourceIconDisplayed() {
        Assert.assertTrue(isBridgeResourceIconDisplayed(), "Bridge resource icon should be displayed");
    }

    public void verifyGreenDotDisplayed() {
        Assert.assertTrue(isGreenDotDisplayed(), "Green dot is displayed for online status");
    }

    public void verifyRedDotDisplayed() {
        Assert.assertTrue(isRedDotDisplayed(), "Red dot is displayed for offline status");
    }

    public void verifyYellowDotDisplayed() {
        Assert.assertTrue(isYellowDotDisplayed(), "Yellow dot is displayed for connectivity issue status");
    }

    public void verifyGreyDotDisplayed() {
        Assert.assertTrue(isGreyDotDisplayed(), "Grey dot is displayed for no service status");
    }

    public void verifyCameraResourceIconDisplayed() {
        Assert.assertTrue(isCameraResourceIconDisplayed(), "Camera resource icon should be displayed");
    }

    public void verifySettingsOptionDisplayed() {
        Assert.assertTrue(isSettingsOptionDisplayed(), "Settings option should be displayed");
    }

    public void verifyDeleteAccountOptionDisplayed() {
        Assert.assertTrue(isDeleteAccountOptionDisplayed(), "Delete account option should be displayed");
    }

    public void verifyFilterByAccountNameDisplayed() {
        Assert.assertTrue(isFilterByAccountNameFilterDisplayed(), "Filter by account name should be displayed");
    }

    public void verifyAddAccountButtonDisplayed() {
        Assert.assertTrue(isAddAccountButtonDisplayed(), "Add account button should be displayed");
    }

    public void verifyDashboardOptionDisplayed() {
        Assert.assertTrue(isDashboardOptionDisplayed(), "Dashboard option should be displayed");
    }

    public void verifyUsersOptionDisplayed() {
        Assert.assertTrue(isUsersOptionDisplayed(), "Users option should be displayed");
    }

    public void verifyBusinessPortalOptionDisplayed() {
        Assert.assertTrue(isBusinessPortalOptionDisplayed(), "Business Portal option should be displayed");
    }

    public void verifyMoreOptionDisplayed() {
        Assert.assertTrue(isMoreOptionDisplayed(), "More option should be displayed");
    }

    public void dismissContextMenu() {
        if (PlatformLocator.isIos()) {
            // iOS: tap the dimmed area beside the menu, which closes it without choosing an option.
            GestureUtils.tapOutsideContextMenu(driver);
            Assert.assertTrue(WaitUtils.waitForInvisibility(driver, ResellerDashboardLocators.CONTEXT_MENU_SETTINGS,
                    TimeoutConstants.SHORT_TIMEOUT), "Context menu should close");
            return;
        }
        ((io.appium.java_client.android.AndroidDriver) driver).pressKey(
            new io.appium.java_client.android.nativekey.KeyEvent(
                io.appium.java_client.android.nativekey.AndroidKey.BACK
            )
        );
    }

    public void verifyAccountNameFieldDisplayed() {
        Assert.assertTrue(isAccountNameFieldDisplayed(), "Account Name field should be displayed");
    }

    public void verifyAddButtonDisplayed() {
        Assert.assertTrue(isAddButtonDisplayed(), "Add button should be displayed");
    }

    public void verifyAddCustomerIdFieldDisplayed() {
        Assert.assertTrue(isAddCustomerIdFieldDisplayed(), "Add customer id field should be displayed");
    }

    public void verifyEmailFieldDisplayed() {
        Assert.assertTrue(isEmailFieldDisplayed(), "Email field should be displayed");
    }

    public void verifyEditionFieldDisplayed() {
        Assert.assertTrue(isEditionFieldDisplayed(), "Edition field should be displayed");
    }

    public void verifyStandardOptionDisplayed() {
        Assert.assertTrue(isStandardOptionDisplayed(), "Standard option should be displayed");
    }

    public void verifyProfessionalOptionDisplayed() {
        Assert.assertTrue(isProfessionalOptionDisplayed(), "Professional option should be displayed");
    }

    public void verifyEnterpriseOptionDisplayed() {
        Assert.assertTrue(isEnterpriseOptionDisplayed(), "Enterprise option should be displayed");
    }

    public void verifyCameraDefaultsSectionDisplayed() {
        Assert.assertTrue(isCameraDefaultsSectionDisplayed(), "Camera defaults section should be displayed");
    }

    public void verifyCloudRetentionOptionDisplayed() {
        Assert.assertTrue(isCloudRetentionOptionDisplayed(), "Cloud retention option should be displayed");
    }

    public void verifyThirtyDaysDefaultOptionDisplayed() {
        Assert.assertTrue(isThirtyDaysDefaultOptionDisplayed(), "Thirty days default option should be displayed");
    }

    public void verifyMinimumOneDayOptionDisplayed() {
        Assert.assertTrue(isMinimumOneDayOptionDisplayed(), "Minimum one day option should be displayed");
    }

    public void verifyMax5YearsOptionDisplayed() {
        Assert.assertTrue(isMax5YearsOptionDisplayed(), "Max 5 years option should be displayed");
    }

    public void verifyMinRetentionOptionDisplayed() {
        Assert.assertTrue(isMinRetentionOptionDisplayed(), "Min retention option should be displayed");
    }

    public void verify14DaysDefaultOptionDisplayed() {
        Assert.assertTrue(is14DaysDefaultOptionDisplayed(), "14 days default option should be displayed");
    }

    public void verifyMinRetentionSectionMinValueNoneDisplayed() {
        Assert.assertTrue(isMinRetentionSectionMinValueNoneDisplayed(), "Min retention section minimum value None should be displayed");
    }

    public void verifyMinRetentionSectionMaxValue180DaysDisplayed() {
        Assert.assertTrue(isMinRetentionSectionMaxValue180DaysDisplayed(), "Min retention section maximum value 180 days should be displayed");
    }

    public void verifyMaxRetentionOptionDisplayed() {
        Assert.assertTrue(isMaxRetentionOptionDisplayed(), "Max retention option should be displayed");
    }

    public void verifySixtyDaysByDefaultOptionDisplayed() {
        Assert.assertTrue(sixtyDaysByDefaultOptionDisplayed(), "60 days by default option should be displayed");
    }

    public void verifyMinimumValueOptionIsNoneInMaxRetentionDisplayed() {
        Assert.assertTrue(minimumValueOptionIsNoneInMaxRetentionDisplayed(), "Minimum value None in max retention should be displayed");
    }

    public void verifyMaxValueOptionIs1YearInMaxRetentionDisplayed() {
        Assert.assertTrue(maxValueOptionIs1YearInMaxRetentionDisplayed(), "Maximum value 1 year in max retention should be displayed");
    }

    public void verifyPreviewResolutionOptionDisplayed() {
        Assert.assertTrue(previewResolutionOptionIsDisplayed(), "Preview resolution option should be displayed");
    }

    public void verifyDefaultPreviewResolutionOptionDisplayed() {
        Assert.assertTrue(defaultpreviewResolutionOptionIsDisplayed(), "Default preview resolution option should be displayed");
    }

    public void verifyFullVideoResolutionOptionDisplayed() {
        Assert.assertTrue(fullVideoResolutionOptionIsDisplayed(), "Full video resolution option should be displayed");
    }

    public void verifyFullVideoResolutionDefaultValueDisplayed() {
        Assert.assertTrue(fullVideoResolutionDefaultValueIsDisplayed(), "Full video resolution default value should be displayed");
    }

    public void verifyCloudPreviewOnlyToggleButtonDisplayed() {
        Assert.assertTrue(cloudPreviewOnlyToggleButton(), "Cloud preview only toggle button should be displayed");
    }

    public void verifyMax120CharsErrorMessageDisplayed() {
        Assert.assertTrue(isMax120CharsErrorMessageDisplayed(), "Max 120 characters error message should be displayed");
    }

    public void verifySuccessMessageDisplayed() {
        Assert.assertTrue(isSuccessMessageDisplayed(), "Success message should be displayed after account creation");
    }

    public boolean isDeletedSuccessMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, ResellerDashboardLocators.DELETED_SUCCESS_MESSAGE, Duration.ofSeconds(60)).isDisplayed();
    }

    public void verifyDeletedSuccessMessageDisplayed() {
        Assert.assertTrue(isDeletedSuccessMessageDisplayed(), "Deleted successfully message should be displayed after account deletion");
    }

    // ---- step-level verification workflows --------------------------------------------------

    public void verifyDashboardSummary() {
        verifyTotalAccountsDisplayed();
    }

    public void verifyAccountCardContents(String bridgeAccountName) {
        verifyLargeModelTransformationIconDisplayed();
        verifyAccountNameDisplayed(bridgeAccountName);
        verifyBridgeResourceIconDisplayed();
        verifyCameraResourceIconDisplayed();
        verifyGreenDotDisplayed();
        verifyRedDotDisplayed();
        verifyYellowDotDisplayed();
        verifyGreyDotDisplayed();
    }

    public void verifyContextMenuOptions() {
        clickThreeDotsOption();
        verifySettingsOptionDisplayed();
        verifyDeleteAccountOptionDisplayed();
        dismissContextMenu();
    }

    public void verifyDashboardToolbar() {
        verifyFilterByAccountNameDisplayed();
        verifyAddAccountButtonDisplayed();
    }

    public void verifyBottomNavigationBar() {
        verifyDashboardOptionDisplayed();
        verifyUsersOptionDisplayed();
        verifyBusinessPortalOptionDisplayed();
        verifyMoreOptionDisplayed();
    }

    public void verifyAddAccountFormFields() {
        verifyAddAccountButtonDisplayed();
        clickAddAccountButton();
        verifyAccountNameFieldDisplayed();
        verifyAddButtonDisplayed();
        verifyAddCustomerIdFieldDisplayed();
        verifyEmailFieldDisplayed();
        verifyEditionFieldDisplayed();
        verifyStandardOptionDisplayed();
    }

    public void verifyEditionOptions() {
        clickEditionSectionNavigationIcon();
        verifyProfessionalOptionDisplayed();
        verifyEnterpriseOptionDisplayed();
        clickEditionSectionBackNavigationIcon();
    }

    public void verifyCloudRetentionRange() {
        verifyCameraDefaultsSectionDisplayed();
        verifyCloudRetentionOptionDisplayed();
        verifyThirtyDaysDefaultOptionDisplayed();
        cloudRetentionSectionNavigationIcon();
        verifyMinimumOneDayOptionDisplayed();
        GestureUtils.swipeUp(driver);
        verifyMax5YearsOptionDisplayed();
        clickCloudRetentionSectionBackNavigationIcon();
    }

    public void verifyMinRetentionRange() {
        verifyMinRetentionOptionDisplayed();
        verify14DaysDefaultOptionDisplayed();
        clickMinRetentionSectionViewIcon();
        verifyMinRetentionSectionMinValueNoneDisplayed();
        GestureUtils.swipeUp(driver);
        verifyMinRetentionSectionMaxValue180DaysDisplayed();
        clickMinRetentionSectionBackIcon();
    }

    public void verifyMaxRetentionRange() {
        verifyMaxRetentionOptionDisplayed();
        verifySixtyDaysByDefaultOptionDisplayed();
        clickMaxRetentionSectionViewIcon();
        verifyMinimumValueOptionIsNoneInMaxRetentionDisplayed();
        GestureUtils.swipeUp(driver);
        verifyMaxValueOptionIs1YearInMaxRetentionDisplayed();
        clickMaxRetentionSectionBackIcon();
    }

    public void verifyResolutionAndToggleDefaults() {
        verifyPreviewResolutionOptionDisplayed();
        verifyDefaultPreviewResolutionOptionDisplayed();
        verifyFullVideoResolutionOptionDisplayed();
        verifyFullVideoResolutionDefaultValueDisplayed();
        verifyCloudPreviewOnlyToggleButtonDisplayed();
    }

    public void verifyCustomerIdTooLongIsRejected(String tooLongId, String accountName) {
        enterCustomerId(tooLongId);
        enterAccountName(accountName);
        clickAddOption();
        verifyMax120CharsErrorMessageDisplayed();
    }

    public void submitNewAccount(String accountName, String customerId, String email) {
        enterAccountName(accountName);
        enterCustomerId(customerId);
        clickEmailField();
        enterEmail(email);
        clickAddOption();
    }

    public void verifyAccountCreatedAndVisible(String fullAccountName) {
        verifySuccessMessageDisplayed();
        scrollUntilAccountIsVisible(fullAccountName);
        verifyAccountNameDisplayed(fullAccountName);
    }
}
