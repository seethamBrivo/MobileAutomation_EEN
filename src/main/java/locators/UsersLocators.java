package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Locators for the Users screens: the users list, the new-user form and the user details / delete flow.
 * <p>
 * Many locators are unions (XPath {@code |}) of the old view-based id and the new Compose structure, so
 * either app build keeps working.
 */
public final class UsersLocators {

    private UsersLocators() {}

    private static final String END_ICON_DESC = "_user_details_container_end_icon";

    // --- Users list and navigation ----------------------------------------------

    public static final By USERS_OPTION_AT_THE_BOTTOM_OF_THE_SCREEN =
            AppiumBy.xpath("//android.widget.TextView[@text='Users']");

    // The Users screen is Compose now (UsersMainScreen). Its header is [title, sort, add] with no ids,
    // so the add icon is the last clickable sibling after the title. The old view-based id is kept
    // in the union so an older app build still works.
    public static final By USERS_ADD_ICON_AT_THE_TOP_RIGHT_CORNER = AppiumBy.xpath(
            "//*[@resource-id='com.een.vms.android:id/img_custom']"
                    + " | "+"//androidx.compose.ui.platform.ComposeView[@content-desc='UsersMainScreen']//android.widget.TextView[@text='Users']"
                    + "/following-sibling::android.view.View[@clickable='true'][last()]");

    public static final By USERS_SEARCH_FILTER_ICON =
            AppiumBy.id("com.een.vms.android:id/img_search");

    // New UI: the filter field is always on the list (no search icon / separate search screen).
    public static final By FILTER_FOR_USERS_SEARCHBAR = AppiumBy.xpath(
            "//*[@resource-id='com.een.vms.android:id/search_bar']"
                    + " | //androidx.compose.ui.platform.ComposeView[@content-desc='UsersMainScreen']//android.widget.EditText");

    public static final By BACK_BUTTON =
            AppiumBy.id("com.een.vms.android:id/img_back");

    // --- New-user form ----------------------------------------------------------

    // New-user form (Compose): header is [back, "New user", Add]; each field is an EditText that follows its
    // label TextView. The previous resource-id / content-desc locators are kept in the unions.
    public static final By NEW_USER_HEADING =
            AppiumBy.xpath("//android.widget.TextView[@text='New user']");

    public static final By EMAIL_TEXT_INPUT_FIELD = AppiumBy.xpath("//*[@content-desc='email_text_field']"
            + " | //android.widget.TextView[@text='Email']/following-sibling::android.widget.EditText[1]");

    public static final By ADD_BUTTON = AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/btn_text']"
            + " | //android.widget.TextView[@text='New user']/following-sibling::android.view.View[@clickable='true'][last()]");

    public static final By FIRST_NAME_TEXT_FIELD = AppiumBy.xpath("//*[@content-desc='first_name_text_field']"
            + " | //android.widget.TextView[@text='First name']/following-sibling::android.widget.EditText[1]");

    public static final By LAST_NAME_TEXT_FIELD = AppiumBy.xpath("//*[@content-desc='last_name_text_field']"
            + " | //android.widget.TextView[@text='Last name']/following-sibling::android.widget.EditText[1]");

    public static final By EMAIL_PLUS_HINT = AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/emailPlusHint']"
            + " | //android.widget.TextView[starts-with(@text,'The + alias is no longer required')]");

    // New UI: the snackbar is a plain TextView with this text. The old positional Compose path is kept in the union.
    public static final By SNACKBAR_TEXT = AppiumBy.xpath("//android.widget.TextView[@text='User was added']"
            + " | //androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[3]");

    // --- Delete flow ------------------------------------------------------------

    // New UI: a clickable row on the user's details screen with a "Delete user" label.
    public static final By DELETE_USER_OPTION = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='delete_header_content']"
            + " | //android.widget.TextView[@text='Delete user']/ancestor::android.view.View[@clickable='true'][1]");

    // New UI: the confirmation dialog's "DELETE" action.
    public static final By CONFIRM_DELETE_USER_OPTION = AppiumBy.xpath("//android.widget.Button[@content-desc='negative_button']"
            + " | //android.widget.TextView[@text='DELETE']/ancestor::android.view.View[@clickable='true'][1]");

    // --- Dynamic locators -------------------------------------------------------

    /** The list row showing exactly this e-mail. */
    public static By userRowText(String email) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']");
    }

    /**
     * The end (details) icon inside the same row as {@code email}: the nearest ancestor of the
     * e-mail label that also contains an end icon is the row itself.
     */
    public static By userRowEndIcon(String email) {
        return AppiumBy.xpath("//android.widget.TextView[@text='" + email + "']"
                + "/ancestor::*[.//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']][1]"
                + "//android.widget.ImageView[@content-desc='" + END_ICON_DESC + "']"
                // New UI: the whole row is the clickable target.
                + " | //android.widget.TextView[@text='" + email + "']"
                + "/ancestor::android.view.View[@clickable='true'][1]");
    }
}
