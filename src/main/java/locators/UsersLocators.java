package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;

/**
 * Locators for the Users screens: the users list, the new-user form and the user details / delete flow.
 * <p>
 * Many locators are unions (XPath {@code |}) of the old view-based id and the new Compose structure, so
 * either app build keeps working.
 */
public final class UsersLocators {

    private UsersLocators() {}

    private static final String END_ICON_DESC = "_user_details_container_end_icon";

    private static final String IOS_ROW = "//XCUIElementTypeCell[starts-with(@name, 'cell_at_')]";

    // iOS: the e-mail is in the label ("e-mail . status"); the name is only an id.
    private static String iosSubtitleOf(String email) {
        return "//XCUIElementTypeStaticText[contains(@name, '_users_subtitle') and starts-with(@label, '"
                + email + " ')]";
    }

    // --- Users list and navigation ----------------------------------------------

    // The Users screen is Compose now (UsersMainScreen). Its header is [title, sort, add] with no ids,
    // so the add icon is the last clickable sibling after the title. The old view-based id is kept
    // in the union so an older app build still works.
    public static final By USERS_ADD_ICON_AT_THE_TOP_RIGHT_CORNER = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/img_custom']"
                    + " | "+"//androidx.compose.ui.platform.ComposeView[@content-desc='UsersMainScreen']//android.widget.TextView[@text='Users']"
                    + "/following-sibling::android.view.View[@clickable='true'][last()]"), // android
            AppiumBy.accessibilityId("navigationBar_customButton")); // ios

    public static final By USERS_SEARCH_FILTER_ICON =
            AppiumBy.id("com.een.vms.android:id/img_search");

    // New UI: the filter field is always on the list (no search icon / separate search screen).
    public static final By FILTER_FOR_USERS_SEARCHBAR = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/search_bar']"
                    + " | //androidx.compose.ui.platform.ComposeView[@content-desc='UsersMainScreen']//android.widget.EditText"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios

    public static final By BACK_BUTTON = PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/img_back"), // android
            AppiumBy.accessibilityId("BackButton")); // ios

    // --- New-user form ----------------------------------------------------------

    // New-user form (Compose): header is [back, "New user", Add]; each field is an EditText that follows its
    // label TextView. The previous resource-id / content-desc locators are kept in the unions.
    public static final By NEW_USER_HEADING = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='New user']"), // android
            AppiumBy.xpath("//XCUIElementTypeNavigationBar[@name='New user']")); // ios

    // TODO: the iOS form fields have no ids; replace these row-based XPaths when the app adds them.
    public static final By EMAIL_TEXT_INPUT_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//*[@content-desc='email_text_field']"
                    + " | //android.widget.TextView[@text='Email']/following-sibling::android.widget.EditText[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeCell[@name='email']//XCUIElementTypeTextField")); // ios

    public static final By ADD_BUTTON = PlatformLocator.get(
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/btn_text']"
                    + " | //android.widget.TextView[@text='New user']/following-sibling::android.view.View[@clickable='true'][last()]"), // android
            USERS_ADD_ICON_AT_THE_TOP_RIGHT_CORNER); // ios

    public static final By FIRST_NAME_TEXT_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//*[@content-desc='first_name_text_field']"
                    + " | //android.widget.TextView[@text='First name']/following-sibling::android.widget.EditText[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeCell[@name='name']//XCUIElementTypeTextField")); // ios

    public static final By LAST_NAME_TEXT_FIELD = PlatformLocator.get(
            AppiumBy.xpath("//*[@content-desc='last_name_text_field']"
                    + " | //android.widget.TextView[@text='Last name']/following-sibling::android.widget.EditText[1]"), // android
            AppiumBy.xpath("//XCUIElementTypeCell[@name='lastName']//XCUIElementTypeTextField")); // ios

    public static final By EMAIL_PLUS_HINT = AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/emailPlusHint']"
            + " | //android.widget.TextView[starts-with(@text,'The + alias is no longer required')]");

    // New UI: the snackbar is a plain TextView with this text. The old positional Compose path is kept in the union.
    public static final By SNACKBAR_TEXT = AppiumBy.xpath("//android.widget.TextView[@text='"
            + UiTextConstants.USER_ADDED_MESSAGE + "']"
            + " | //androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[3]");

    // --- Delete flow ------------------------------------------------------------

    // New UI: a clickable row on the user's details screen with a "Delete user" label.
    public static final By DELETE_USER_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.view.ViewGroup[@content-desc='delete_header_content']"
                    + " | //android.widget.TextView[@text='Delete user']/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.xpath("//XCUIElementTypeCell[@name='delete']")); // ios

    // New UI: the confirmation dialog's "DELETE" action.
    public static final By CONFIRM_DELETE_USER_OPTION = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']"
                    + " | //android.widget.TextView[@text='DELETE']/ancestor::android.view.View[@clickable='true'][1]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert[@name='Delete user']//XCUIElementTypeButton[@name='Delete']")); // ios

    // TODO: the iOS details title has no id; replace this navigation bar match when the app adds one.
    public static final By IOS_DETAILS_TITLE =
            AppiumBy.xpath("//XCUIElementTypeNavigationBar/XCUIElementTypeStaticText");

    // --- Dynamic locators -------------------------------------------------------

    /** The list row showing exactly this e-mail. */
    public static By userRowText(String email) {
        if (PlatformLocator.isIos()) {
            return AppiumBy.xpath(IOS_ROW + iosSubtitleOf(email));
        }
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']");
    }

    /**
     * The end (details) icon inside the same row as {@code email}: the nearest ancestor of the
     * e-mail label that also contains an end icon is the row itself.
     */
    public static By userRowEndIcon(String email) {
        if (PlatformLocator.isIos()) {
            return AppiumBy.xpath(IOS_ROW + "[." + iosSubtitleOf(email) + "]");
        }
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']"
                + "/ancestor::*[.//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']][1]"
                + "//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']"
                // New UI: the whole row is the clickable target.
                + " | //android.widget.TextView[@text='" + email + "']"
                + "/ancestor::android.view.View[@clickable='true'][1]");
    }

    public static By iosUserRowName(String email) {
        return AppiumBy.xpath(IOS_ROW + "[." + iosSubtitleOf(email) + "]"
                + "//XCUIElementTypeStaticText[contains(@name, '_users_name')]");
    }
}
