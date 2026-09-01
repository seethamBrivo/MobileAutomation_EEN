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
        try {
            // Wait longer for the popup to appear after login (BrowserStack is slower)
            Thread.sleep(3000);

            // Try multiple strategies to find and click the Allow/No Thanks button
            boolean clicked = false;

            // Strategy 1: Using the positive_button resource-id with custom longer wait
            if (!clicked) {
                try {
                    org.openqa.selenium.support.ui.WebDriverWait wait =
                        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30));
                    wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(
                        noThanksOptionInBiometricsAuthenticationPopUp)).click();
                    System.out.println("✓ Clicked Allow button (positive_button resource-id)");
                    clicked = true;
                } catch (Exception e) {
                    System.out.println("Strategy 1 failed, trying alternative...");
                }
            }

            // Strategy 2: Find by text "Allow"
            if (!clicked) {
                try {
                    driver.findElement(AppiumBy.xpath("//android.widget.Button[@text='Allow']")).click();
                    System.out.println("✓ Clicked Allow button (text xpath)");
                    clicked = true;
                } catch (Exception e) {
                    System.out.println("Strategy 2 failed, trying alternative...");
                }
            }

            // Strategy 3: Find by text "NO THANKS" or "No Thanks"
            if (!clicked) {
                try {
                    driver.findElement(AppiumBy.xpath("//android.widget.Button[contains(@text, 'NO THANKS') or contains(@text, 'No Thanks')]")).click();
                    System.out.println("✓ Clicked No Thanks button");
                    clicked = true;
                } catch (Exception e) {
                    System.out.println("Strategy 3 failed - popup may not have appeared");
                }
            }

            if (!clicked) {
                System.out.println("⚠ Biometrics popup did not appear or was already dismissed");
            }

            // Small wait after clicking
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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