package pages;
import constants.TimeoutConstants;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ConfigReader;
import utils.GestureUtils;
import utils.PlatformLocator;
import utils.WaitUtils;

import java.time.Duration;

public class ResellerDashboardPage {
    private final AppiumDriver driver;
    private final By resellerDashboard = PlatformLocator.byText("Reseller dashboard");
    private final By totalAccounts= PlatformLocator.byTextContains("accounts");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: this dashboard graphic carries no content-desc/testTag yet.
    private final By largeModelTransformationIcon= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View"),
        AppiumBy.accessibilityId("ResellerDashboardMainScreen")  // iOS: Use accessibility ID directly
    );
    private By accountName(String accountName) {
        return PlatformLocator.byText(accountName);
    }

    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the first account row's resource icons and status dots carry no
    // content-desc/testTag yet, so each is addressed by its sibling index in the row.
    private final By bridgeResourceIcon= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[1]"),
        AppiumBy.accessibilityId("bridgeResourceIcon")  // iOS: Add accessibility ID once available
    );
    private final By greenDotForOnline= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[2]"),
        AppiumBy.accessibilityId("statusDotOnline")
    );
    private final By redDotForOffline= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[3]"),
        AppiumBy.accessibilityId("statusDotOffline")
    );
    private final By yellowDotForConnectivityIssue= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[7]"),
        AppiumBy.accessibilityId("statusDotConnectivityIssue")
    );
    private final By greyDotForStatusTurnedOff= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[8]"),
        AppiumBy.accessibilityId("statusDotTurnedOff")
    );
    private final By cameraResourceIcon= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[4]"),
        AppiumBy.accessibilityId("cameraResourceIcon")
    );
    private final By threeDotsOption= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[9]"),
        AppiumBy.accessibilityId("moreOptions")
    );
    private final By settingsOption= PlatformLocator.byText("Settings");
    private final By deleteAccountOption= PlatformLocator.byText("Delete account");
    private final By filterByAccountNameFilter= PlatformLocator.byText("Filter by account name");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: this button carries no content-desc/testTag yet.
    private final By addAccountButton= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View/android.view.View"),
        AppiumBy.accessibilityId("addAccountButton")
    );
    private final By dashboardOption= PlatformLocator.byText("Dashboard");
    private final By usersOption= PlatformLocator.byText("Users");
    private final By businessPortalOption= PlatformLocator.byText("Business Portal");
    private final By moreOption= PlatformLocator.byText("More");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the first account row in the list carries no content-desc/testTag yet.
    private final By firstAccountWithBridges = PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='ResellerDashboardMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]"),
        AppiumBy.accessibilityId("firstAccountRow")
    );
    private final By layoutsScreen = PlatformLocator.byText("Layouts");
    private final By logoutOption= PlatformLocator.byText("Log out");
    private final By confirmLogoutButton= PlatformLocator.byText("YES");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: the add-account form's EditTexts are addressed by their position in the
    // ScrollView, since none carries a resource-id or testTag.
    private final By accountNameField= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[2]/XCUIElementTypeTextField")
    );
    private final By addButton= PlatformLocator.byText("Add");
    private final By addCustomerIdField= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.EditText"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[3]/XCUIElementTypeTextField")
    );
    private final By emailField= PlatformLocator.byText("Add email");
    private final By editionField= PlatformLocator.byText("Edition");
    private final By standardByDefault= PlatformLocator.byText("Standard");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: addressed by its position in the ScrollView, since it carries neither.
    private final By editionSectionNavigationIcon= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[5]/android.view.View"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[5]/XCUIElementTypeOther")
    );
    private final By professionalOption= PlatformLocator.byText("Professional");
    private final By enterpriseOption= PlatformLocator.byText("Enterprise");
    // TODO: Replace with testTag when mobile team adds proper Compose testTag attributes.
    // Positional: the generic back-chevron icon reused across every settings sub-panel
    // (edition, cloud retention, min retention, max retention) — same element each time,
    // so a single field is clicked from clickEditionSectionBackNavigationIcon(),
    // clickCloudRetentionSectionBackNavigationIcon(), clickMinRetentionSectionBackIcon()
    // and clickMaxRetentionSectionBackIcon().
    private final By genericBackChevron= PlatformLocator.get(
        AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View"),
        AppiumBy.accessibilityId("backButton")
    );
    private final By cameraDefaultsSection= PlatformLocator.byText("CAMERAS DEFAULTS");
    private final By cloudRetention= PlatformLocator.byText("Cloud retention");
    private final By thirtyDaysDefault= PlatformLocator.byText("30 days");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: addressed by its position in the ScrollView, since it carries neither.
    private final By cloudRetentionSectionNavigationIcon= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[7]/android.view.View"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[7]/XCUIElementTypeOther")
    );
    private final By minimumOneDayOption= PlatformLocator.byText("1 day (M10)");
    private final By max5YearsRetentionOption= PlatformLocator.byText("5 years");
    private final By minRetention= PlatformLocator.byText("Min retention");
    private final By fourTeenDaysDefault= PlatformLocator.byText("14 days");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: addressed by its position in the ScrollView, since it carries neither.
    private final By minRetentionViewIcon= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[8]/android.view.View"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[8]/XCUIElementTypeOther")
    );
    private final By miniumRetentionMinimumValueNone= PlatformLocator.byText("None");
    private final By max180DaysOption= PlatformLocator.byText("180 days");
    private final By maxRetentionOption= PlatformLocator.byText("Max retention");
    private final By sixtyDaysByDafaultOptionForMaxRetention= PlatformLocator.byText("60 days");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: addressed by its position in the ScrollView, since it carries neither.
    private final By maxRetentionViewIcon= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[9]/android.view.View"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[9]/XCUIElementTypeOther")
    );
    private final By minValueIsNoneInMaxRetention= PlatformLocator.byText("None");
    private final By maxValueIs1YearInMaxRetention= PlatformLocator.byText("1 year");
    private final By previewResolutionOption= PlatformLocator.byText("Preview resolution");
    private final By defaultPreviewResolutionOption= PlatformLocator.byText("cif (SD1 320x240)");
    private final By fullVideoResolutionOption= PlatformLocator.byText("Full video resolution");
    private final By fullVideoResolutionDafaultValue= PlatformLocator.byText("high (HD1 1280x720)");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: addressed by its position in the ScrollView, since it carries neither.
    private final By cloudPreviewOnlyoggleButton= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[12]/android.view.View/android.view.View"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[12]/XCUIElementTypeOther/XCUIElementTypeOther")
    );
    private final By max120CharErrorMessage= PlatformLocator.byText("Max 120 characters");
    private final By successMessage= PlatformLocator.byText("Added successfully");
    private final By deleteConfirmButton= PlatformLocator.byText("DELETE");
    private final By deletedSuccessMessage= PlatformLocator.byText("Deleted successfully");
    // TODO: Replace with testTag when mobile team adds proper resource-id/testTag attributes.
    // Positional: Email input field within the ScrollView after clicking "Add email"
    private final By emailInputField= PlatformLocator.get(
        AppiumBy.xpath("//android.widget.ScrollView/android.view.View[4]/android.widget.EditText"),
        AppiumBy.iOSClassChain("**/XCUIElementTypeScrollView/XCUIElementTypeOther[4]/XCUIElementTypeTextField")
    );

    public ResellerDashboardPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardPageDisplayed() {
        return WaitUtils.waitForVisibility(driver, resellerDashboard).isDisplayed();
    }

    public void clickFirstAccountWithBridges() {
        WaitUtils.waitForClickability(driver, firstAccountWithBridges).click();
    }

    public boolean isLayoutsScreenDisplayed() {
        return WaitUtils.waitForVisibility(driver, layoutsScreen).isDisplayed();
    }

    public boolean isTotalAccountsDisplayed() {
        return WaitUtils.waitForVisibility(driver, totalAccounts).isDisplayed();
    }

    public boolean isLargeModelTransformationIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, largeModelTransformationIcon).isDisplayed();
    }

    public boolean isAccountNameDisplayed(String accountName) {
        return WaitUtils.waitForVisibility(
                driver,
                accountName(accountName)
        ).isDisplayed();
    }

    public void scrollUntilAccountIsVisible(String accountName) {
        GestureUtils.scrollUntilPresent(driver, accountName(accountName));
    }

    public boolean isBridgeResourceIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, bridgeResourceIcon).isDisplayed();
    }

    public boolean isGreenDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, greenDotForOnline).isDisplayed();
    }

    public boolean isRedDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, redDotForOffline).isDisplayed();
    }

    public boolean isYellowDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, yellowDotForConnectivityIssue).isDisplayed();
    }

    public boolean isGreyDotDisplayed() {
        return WaitUtils.waitForVisibility(driver, greyDotForStatusTurnedOff).isDisplayed();
    }

    public boolean isCameraResourceIconDisplayed() {
        return WaitUtils.waitForVisibility(driver, cameraResourceIcon).isDisplayed();
    }

    public  void clickThreeDotsOption() {
        WaitUtils.waitForVisibility(driver, threeDotsOption).click();
    }

    public boolean isSettingsOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, settingsOption).isDisplayed();
    }

    public boolean isDeleteAccountOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, deleteAccountOption).isDisplayed();
    }

    public boolean isFilterByAccountNameFilterDisplayed() {
        return WaitUtils.waitForVisibility(driver, filterByAccountNameFilter).isDisplayed();
    }

    public boolean isAddAccountButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, addAccountButton).isDisplayed();
    }

    public boolean isDashboardOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, dashboardOption).isDisplayed();
    }

    public boolean isUsersOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, usersOption).isDisplayed();
    }

    public boolean isBusinessPortalOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, businessPortalOption).isDisplayed();
    }

    public boolean isMoreOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, moreOption).isDisplayed();
    }

    public  void clickAddAccountButton() {
        WaitUtils.waitForVisibility(driver, addAccountButton).click();
    }

    public boolean isAccountNameFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, accountNameField).isDisplayed();
    }

    public boolean isAddButtonDisplayed() {
        return WaitUtils.waitForVisibility(driver, addButton).isDisplayed();
    }

    public boolean isAddCustomerIdFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, addCustomerIdField).isDisplayed();
    }

    public boolean isEmailFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, emailField).isDisplayed();
    }

    public boolean isEditionFieldDisplayed() {
        return WaitUtils.waitForVisibility(driver, editionField).isDisplayed();
    }

    public boolean isStandardOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, standardByDefault).isDisplayed();
    }

    public  void clickEditionSectionNavigationIcon() {
        WaitUtils.waitForVisibility(driver, editionSectionNavigationIcon).click();
    }

    public boolean isProfessionalOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, professionalOption).isDisplayed();
    }

    public boolean isEnterpriseOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, enterpriseOption).isDisplayed();
    }

    public void clickEditionSectionBackNavigationIcon() {
        WaitUtils.waitForVisibility(driver, genericBackChevron).click();
    }

    public boolean isCameraDefaultsSectionDisplayed() {
        return WaitUtils.waitForVisibility(driver, cameraDefaultsSection).isDisplayed();
    }

    public boolean isCloudRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, cloudRetention).isDisplayed();
    }

    public boolean isThirtyDaysDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, thirtyDaysDefault).isDisplayed();
    }

    public void cloudRetentionSectionNavigationIcon() {
        WaitUtils.waitForVisibility(driver, cloudRetentionSectionNavigationIcon).click();
    }

    public boolean isMinimumOneDayOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, minimumOneDayOption).isDisplayed();
    }

    public boolean isMax5YearsOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, max5YearsRetentionOption).isDisplayed();
    }

    public void clickCloudRetentionSectionBackNavigationIcon() {
        WaitUtils.waitForVisibility(driver, genericBackChevron).click();
    }

    public boolean isMinRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, minRetention).isDisplayed();
    }

    public boolean is14DaysDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, fourTeenDaysDefault).isDisplayed();
    }

    public void clickMinRetentionSectionViewIcon() {
        WaitUtils.waitForVisibility(driver, minRetentionViewIcon).click();
       }
    public boolean isMinRetentionSectionMinValueNoneDisplayed() {
        return WaitUtils.waitForVisibility(driver, miniumRetentionMinimumValueNone).isDisplayed();
      }
    public boolean isMinRetentionSectionMaxValue180DaysDisplayed() {
        return WaitUtils.waitForVisibility(driver, max180DaysOption).isDisplayed();
     }
    public void clickMinRetentionSectionBackIcon() {
        WaitUtils.waitForVisibility(driver, genericBackChevron).click();
      }
    public boolean isMaxRetentionOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, maxRetentionOption).isDisplayed();
     }
    public boolean sixtyDaysByDefaultOptionDisplayed() {
        return WaitUtils.waitForVisibility(driver, sixtyDaysByDafaultOptionForMaxRetention).isDisplayed();
    }

    public void clickMaxRetentionSectionViewIcon() {
        WaitUtils.waitForVisibility(driver, maxRetentionViewIcon).click();
     }
    public boolean minimumValueOptionIsNoneInMaxRetentionDisplayed() {
        return WaitUtils.waitForVisibility(driver, minValueIsNoneInMaxRetention).isDisplayed();
       }
    public boolean maxValueOptionIs1YearInMaxRetentionDisplayed() {
        return WaitUtils.waitForVisibility(driver, maxValueIs1YearInMaxRetention).isDisplayed();
      }
    public void clickMaxRetentionSectionBackIcon() {
        WaitUtils.waitForVisibility(driver, genericBackChevron).click();
    }

    public boolean previewResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, previewResolutionOption).isDisplayed();
     }
    public boolean defaultpreviewResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, defaultPreviewResolutionOption).isDisplayed();
    }

    public boolean fullVideoResolutionOptionIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, fullVideoResolutionOption).isDisplayed();
       }
    public boolean fullVideoResolutionDefaultValueIsDisplayed() {
        return WaitUtils.waitForVisibility(driver, fullVideoResolutionDafaultValue).isDisplayed();
      }
    public boolean cloudPreviewOnlyToggleButton() {
        return WaitUtils.waitForVisibility(driver, cloudPreviewOnlyoggleButton).isDisplayed();
     }
    public void enterCustomerId(String customerID) {
        WebElement field = WaitUtils.waitForVisibility(driver, addCustomerIdField);
        field.clear();
        field.sendKeys(customerID);
    }

    public void enterAccountName(String accountName) {
        WebElement field = WaitUtils.waitForVisibility(driver, accountNameField);
        field.clear();
        field.sendKeys(accountName);
    }

    public void clickAddOption() {
        WaitUtils.waitForVisibility(driver, addButton).click();
       }
    public boolean isMax120CharsErrorMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, max120CharErrorMessage).isDisplayed();
    }

    public boolean isSuccessMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, successMessage, Duration.ofSeconds(60)).isDisplayed();
    }

    public void clickEmailField() {
        WaitUtils.waitForVisibility(driver, emailField).click();
    }

    public void enterEmail(String email) {
        WaitUtils.waitForVisibility(driver, emailInputField).sendKeys(email);
    }

    public void clickBackButton() {
        WaitUtils.waitForVisibility(driver, genericBackChevron).click();
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
        By accountLocator;
        String platform = ConfigReader.getPlatform();

        if ("ios".equalsIgnoreCase(platform)) {
            accountLocator = AppiumBy.iOSNsPredicateString(
                String.format("label == '%s' OR name == '%s'",
                    accountNameWithCustomerId.replace("'", "\\'"),
                    accountNameWithCustomerId.replace("'", "\\'")));
        } else {
            String xpathSafeText = buildXPathStringLiteral(accountNameWithCustomerId);
            accountLocator = AppiumBy.xpath("(//android.widget.TextView[@text=" + xpathSafeText + "])[1]");
        }

        WebElement accountElement = WaitUtils.waitForVisibility(driver, accountLocator);
        GestureUtils.longPress(driver, accountElement);
    }

    /** Backs out of any add/edit screen so the account list (bottom navigation) is showing. */
    public void recoverToAccountList() {
        for (int i = 0; i < 3 && !WaitUtils.isPresent(driver, dashboardOption); i++) {
            driver.navigate().back();
        }
    }

    public void clickDeleteAccountOption() {
        WaitUtils.waitForClickability(driver, deleteAccountOption).click();
    }

    public void clickDeleteConfirmButton() {
        WaitUtils.waitForClickability(driver, deleteConfirmButton).click();
    }

    public boolean isDeletedSuccessMessageDisplayed() {
        return WaitUtils.waitForVisibility(driver, deletedSuccessMessage, Duration.ofSeconds(60)).isDisplayed();
    }

    /**
     * Logs out from wherever the test finished. Bottom navigation is reachable from most
     * screens; nested screens/dialogs are unwound with Back first. Throws if it cannot get
     * there so BaseTest can fall back to clearing the app.
     */
    public void logout() {
        if (!WaitUtils.isVisible(driver, dashboardOption, TimeoutConstants.SHORT_TIMEOUT)) {
            for (int i = 0; i < 5 && !WaitUtils.isPresent(driver, moreOption); i++) {
                driver.navigate().back();
            }
        }
        WaitUtils.click(driver, moreOption);
        if (!WaitUtils.isPresent(driver, logoutOption)) {
            GestureUtils.swipeUp(driver);
        }
        WaitUtils.click(driver, logoutOption);
        WaitUtils.click(driver, confirmLogoutButton);
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
        String platform = ConfigReader.getPlatform();
        if ("ios".equalsIgnoreCase(platform)) {
            // iOS: Tap outside the menu or use escape gesture
            driver.navigate().back();
        } else {
            // Android: Use back key
            ((io.appium.java_client.android.AndroidDriver) driver).pressKey(
                new io.appium.java_client.android.nativekey.KeyEvent(
                    io.appium.java_client.android.nativekey.AndroidKey.BACK
                )
            );
        }
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

    public void verifyDeletedSuccessMessageDisplayed() {
        Assert.assertTrue(isDeletedSuccessMessageDisplayed(), "Deleted successfully message should be displayed after account deletion");
    }
}
