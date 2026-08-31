package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.WaitUtils;

public class LoginPage {

    private final AppiumDriver driver;

    private final By signInButton = AppiumBy.accessibilityId("sign_in_btn_button");
    private final By emailInputField = AppiumBy.className("android.widget.EditText");
    private final By nextButton = AppiumBy.xpath("//android.widget.Button[@text=\"Next\"]");
    private final By passwordField = AppiumBy.xpath("//android.widget.EditText[@resource-id=\"authentication--input__password\"]");
    private final By signInButtonToLogin = AppiumBy.xpath("//android.widget.Button[@resource-id=\"next\"]");
    private final By noThanksOptionInBiometricsAuthenticationPopUp= AppiumBy.id("com.een.vms.android:id/positive_button");

    public LoginPage(AppiumDriver driver) {
        this.driver = driver;
    }

    public LoginPage tapSignIn() {
        WaitUtils.waitForClickability(driver, signInButton).click();
//        return new HomePage(driver);
//		return null;
		return null;
    }
    
    public LoginPage enterEmail(String username) {
        WaitUtils.waitForVisibility(driver, emailInputField).sendKeys(username);
        return this;
    }
    public LoginPage tapNextButton() {
        WaitUtils.waitForVisibility(driver, nextButton).click();
        return this;
    }
    

    public LoginPage enterPassword(String password) {
        WaitUtils.waitForVisibility(driver, passwordField).sendKeys(password);
        return this;
    }

    public LoginPage tapSignInButtonToLogin() {
        WaitUtils.waitForVisibility(driver, signInButtonToLogin).click();
        return this;
    }
    public LoginPage tapNoThanksOptionInBiometricsAuthenticationPopup() {
        WaitUtils.waitForVisibility(driver, noThanksOptionInBiometricsAuthenticationPopUp).click();
        return this;
    }

    public ResellerDashboardPage loginAs(String username, String password) {
        tapSignIn();
        enterEmail(username);
        tapNextButton();
        enterPassword(password);
        tapSignInButtonToLogin();
        tapNoThanksOptionInBiometricsAuthenticationPopup();
        return new ResellerDashboardPage(driver);
    }
}