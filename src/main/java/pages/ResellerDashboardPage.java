package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.GestureUtils;
import utils.WaitUtils;


public class ResellerDashboardPage {

    private final AppiumDriver driver;
    private final By resellerDashboard = AppiumBy.xpath("//android.widget.TextView[@text=\"Reseller dashboard\"]");
    private final By totalAccounts= AppiumBy.xpath("//android.widget.TextView[contains(@text, 'accounts')]");
    private final By largeModelTransformationIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private By accountName(String accountName) {
        return AppiumBy.xpath(
                "//android.widget.TextView[@text=\"" + accountName + "\"]"
        );
    }
    private final By bridgeResourceIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[1]");
    private final By greenDotForOnline= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[2]");
    private final By redDotForOffline= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[3]");
    private final By yellowDotForConnectivityIssue= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[7]");
    private final By greyDotForStatusTurnedOff= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[8]");
    private final By cameraResourceIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[4]");
    private final By threeDotsOption= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View[9]");
    private final By settingsOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Settings\"]");
    private final By deleteAccountOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Delete account\"]");
    private final By filterByAccountNameFilter= AppiumBy.xpath("//android.widget.TextView[@text=\"Filter by account name\"]");
    private final By addAccountButton= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.view.View/android.view.View");
    private final By dashboardOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Dashboard\"]");
    private final By usersOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Users\"]");
    private final By businessPortalOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Business Portal\"]");
    private final By moreOption= AppiumBy.xpath("//android.widget.TextView[@text=\"More\"]");

    // Account with bridges (first account in the list with bridges)
    private final By firstAccountWithBridges = AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc='ResellerDashboardMainScreen']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]");

    //Logout Section locators
    private final By logoutOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Log out\"]");
    private final By confirmLogoutButton= AppiumBy.xpath("//android.widget.TextView[@text=\"YES\"]");

    //Add Account Section locators
    private final By accountNameField= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]/android.widget.EditText");
    private final By addButton= AppiumBy.xpath("//android.widget.TextView[@text=\"Add\"]");
    private final By addCustomerIdField= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[3]/android.widget.EditText");
    private final By emailField= AppiumBy.xpath("//android.widget.TextView[@text=\"Add email\"]");
    private final By editionField= AppiumBy.xpath("//android.widget.TextView[@text=\"Edition\"]");
    private final By standardByDefault= AppiumBy.xpath("//android.widget.TextView[@text=\"Standard\"]");
    private final By editionSectionNavigationIcon= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[5]/android.view.View");
    private final By professionalOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Professional\"]");
    private final By enterpriseOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Enterprise\"]");
    private final By editionSectionBackNavigationIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private final By cameraDefaultsSection= AppiumBy.xpath("//android.widget.TextView[@text=\"CAMERAS DEFAULTS\"]");
    private final By cloudRetention= AppiumBy.xpath("//android.widget.TextView[@text=\"Cloud retention\"]");
    private final By thirtyDaysDefault= AppiumBy.xpath("//android.widget.TextView[@text=\"30 days\"]");
    private final By cloudRetentionSectionNavigationIcon= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[7]/android.view.View");
    private final By minimumOneDayOption= AppiumBy.xpath("//android.widget.TextView[@text=\"1 day (M10)\"]");
    private final By max5YearsRetetionOption= AppiumBy.xpath("//android.widget.TextView[@text=\"5 years\"]");
    private final By orientationSectionBackNavigationIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private final By minRetention= AppiumBy.xpath("//android.widget.TextView[@text=\"Min retention\"]");
    private final By fourTeenDaysDefault= AppiumBy.xpath("//android.widget.TextView[@text=\"14 days\"]");
    private final By minRetentionViewIcon= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[8]/android.view.View");
    private final By miniumRetentionMinimumValueNone= AppiumBy.xpath("//android.widget.TextView[@text=\"None\"]");
    private final By max180DaysOption= AppiumBy.xpath("//android.widget.TextView[@text=\"180 days\"]");
    private final By minRetetionSectionBackIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private final By maxRetentionOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Max retention\"]");
    private final By sixtyDaysByDafaultOptionForMaxRetention= AppiumBy.xpath("//android.widget.TextView[@text=\"60 days\"]");
    private final By maxRetentionViewIcon= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[9]/android.view.View");
    private final By minValueIsNoneInMaxRetention= AppiumBy.xpath("//android.widget.TextView[@text=\"None\"]");
    private final By maxValueIs1YearInMaxRetention= AppiumBy.xpath("//android.widget.TextView[@text=\"1 year\"]");
    private final By maxRetentionBackIcon= AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ResellerDashboardMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View/android.view.View");
    private final By previewResolutionOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Preview resolution\"]");
    private final By defaultPreviewResolutionOption= AppiumBy.xpath("//android.widget.TextView[@text=\"cif (SD1 320x240)\"]");
    private final By fullVideoResolutionOption= AppiumBy.xpath("//android.widget.TextView[@text=\"Full video resolution\"]");
    private final By fullVideoResolutionDafaultValue= AppiumBy.xpath("//android.widget.TextView[@text=\"high (HD1 1280x720)\"]");
    private final By cloudPreviewOnlyoggleButton= AppiumBy.xpath("//android.widget.ScrollView/android.view.View[12]/android.view.View/android.view.View");
    private final By max120CharErrorMessage= AppiumBy.xpath("//android.widget.TextView[@text=\"Max 120 characters\"]");
    
    
    
    
      
    public ResellerDashboardPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardPageDisplayed() {
        return WaitUtils.waitForVisibility(driver, resellerDashboard).isDisplayed();
    }

    public boolean isResellerDashboardDisplayed() {
        return WaitUtils.waitForVisibility(driver, resellerDashboard).isDisplayed();
    }

    public void clickFirstAccountWithBridges() {
        WaitUtils.waitForClickability(driver, firstAccountWithBridges).click();
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
    public boolean isBridgeResourceIconDisplayed() {
    	return WaitUtils.waitForVisibility(driver, bridgeResourceIcon).isDisplayed();
    }
    public boolean isGreenDotIsDisplayed() {
    	return WaitUtils.waitForVisibility(driver, greenDotForOnline).isDisplayed();
    }
    public boolean isRedDotIsDisplayed() {
    	return WaitUtils.waitForVisibility(driver, redDotForOffline).isDisplayed();
    }
    public boolean isyellowDotIsDisplayed() {
    	return WaitUtils.waitForVisibility(driver, yellowDotForConnectivityIssue).isDisplayed();
    }
    public boolean isGreyDotIsDisplayed() {
    	return WaitUtils.waitForVisibility(driver, greyDotForStatusTurnedOff).isDisplayed();
    }
    public boolean isCameraResourceIconIsDisplayed() {
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
    //Add account section methods
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
    public boolean isStandardOptionIsDisplayed() {
    	return WaitUtils.waitForVisibility(driver, standardByDefault).isDisplayed();
    }
    public  void clickEditionSectionNavigationIcon() {
      	 WaitUtils.waitForVisibility(driver, editionSectionNavigationIcon).click();
    }
    public boolean isProfessionalOptionDisplayed() {
    	return WaitUtils.waitForVisibility(driver, professionalOption).isDisplayed();
    }
    public boolean isEnterPriseOptionDisplayed() {
    	return WaitUtils.waitForVisibility(driver, enterpriseOption).isDisplayed();
    }
    public void clickEditionSectionBackNavigationIcon() {
     	 WaitUtils.waitForVisibility(driver, editionSectionBackNavigationIcon).click();
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
    	return WaitUtils.waitForVisibility(driver, max5YearsRetetionOption).isDisplayed();
    }
    public void clickCloudRetentionSectionBackNavigationIcon() {
   	 WaitUtils.waitForVisibility(driver, orientationSectionBackNavigationIcon).click();
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
    public boolean isMinRetetionSectionMinValueIsNoneDisplayed() {
     	 return WaitUtils.waitForVisibility(driver, miniumRetentionMinimumValueNone).isDisplayed();
      }
    public boolean isMinRetetionSectionMaxValueIs180DaysDisplayed() {
    	 return WaitUtils.waitForVisibility(driver, max180DaysOption).isDisplayed();
     }
    public void clickMinRetentionSectionBackIcon() {
     	 WaitUtils.waitForVisibility(driver, minRetetionSectionBackIcon).click();
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
   	 WaitUtils.waitForVisibility(driver, maxRetentionBackIcon).click();
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
    public void enterCustomerIdMoreThan120Characters(String customerID) {
    	 WaitUtils.waitForVisibility(driver, addCustomerIdField).sendKeys(customerID);
    }
    public void enterAccountName(String accountName) {
   	 WaitUtils.waitForVisibility(driver, accountNameField).sendKeys(accountName);
   }
    public void clickAddOption() {
      	 WaitUtils.waitForVisibility(driver, addButton).click();
       }
    public boolean isMax120CharsErrorMessageDisplayed() {
   	 return WaitUtils.waitForVisibility(driver, max120CharErrorMessage).isDisplayed();
    }

    // Logout methods
    public void clickMoreOption() {
        WaitUtils.waitForVisibility(driver, moreOption).click();
    }

    public void logout() {
        try {
            // Step 1: Navigate to main dashboard screen (where More button is visible)
            try {
                WaitUtils.waitForClickability(driver, dashboardOption).click();
            } catch (Exception e) {
                // Press back button multiple times to get to main screen
                // Save original timeout to restore it later
                java.time.Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
                // Guard against null timeout - use default if not set
                if (originalTimeout == null) {
                    originalTimeout = java.time.Duration.ofSeconds(10);
                }

                try {
                    // Temporarily reduce timeout for quick back navigation checks
                    driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));

                    for (int i = 0; i < 5; i++) {
                        try {
                            // Use short wait to check if More button is visible
                            if (driver.findElement(moreOption).isDisplayed()) {
                                break;
                            }
                        } catch (Exception ex) {
                            driver.navigate().back();
                        }
                    }
                } finally {
                    // Restore original implicit wait once after all iterations
                    driver.manage().timeouts().implicitlyWait(originalTimeout);
                }
            }

            // Step 2: Click More button at bottom
            WaitUtils.waitForClickability(driver, moreOption).click();

            // Step 3: Scroll down to find "Log out" button (if not visible)
            try {
                // Check if logout is visible
                driver.findElement(logoutOption);
            } catch (Exception e) {
                // Not visible, scroll down to bottom
                GestureUtils.swipeUp(driver);
                // Scroll animation is fast, next WaitUtils call will handle timing
            }

            // Step 4: Click "Log out" button
            WaitUtils.waitForClickability(driver, logoutOption).click();

            // Step 5: Click "Yes" on confirmation popup
            // WaitUtils.waitForClickability already waits for popup to appear
            WaitUtils.waitForClickability(driver, confirmLogoutButton).click();

        } catch (Exception e) {
            throw e;  // Re-throw to trigger fallback in BaseTest
        }
    }

}