package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.XPathUtils;

public final class ArchiveLocators {

    private ArchiveLocators() {}

    public static final By FILES_MENU_ITEM =
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Files']");

    public static final By ARCHIVE_MENU_ITEM =
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Archive']");

    public static final By FILES_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Files\"]");

    public static final By PROGRESS_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Progress\"]");

    // Brittle: positional XPath - no resource-id or content-desc available for filter icon
    // Mobile team should add unique identifier to improve test stability
    // NOTE: Different indices for Files tab vs Progress tab!
    public static final By FILTER_ICON_FILES_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[2]/android.view.View");

    public static final By FILTER_ICON_PROGRESS_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]/android.view.View");

    public static final By SEARCH_BAR =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Filter for files or folders\"]");

    // EditText for entering text (not the TextView placeholder above)
    public static final By SEARCH_INPUT =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.EditText");

    // Back arrow button - position differs based on search results
    public static final By BACK_ARROW_BUTTON_WITH_RESULTS =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View");

    public static final By BACK_ARROW_BUTTON_EMPTY_STATE =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View");

    public static final By CANCEL_BUTTON =
            AppiumBy.accessibilityId("Cancel");

    // Brittle: positional XPath - no resource-id or content-desc available
    public static final By THREE_DOTS_MENU =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]");

    public static By searchResultByFileName(String fileName) {
        return AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[contains(@text, " +
            XPathUtils.xpathLiteral(fileName) + ")]");
    }

    public static final By FILE_ITEM =
            AppiumBy.id("com.een.vms.android:id/file_item");

    public static final By EMPTY_STATE =
            AppiumBy.xpath("//android.widget.TextView[@text=\"No results found\"]");

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_FOLDER_THREE_DOTS =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]");

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_FOLDER =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]");

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_VIDEO_THREE_DOTS =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View[2]");

    public static final By DETAILS_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Details\"]");

    public static final By DOWNLOAD_OPTION =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Download\"]");

    public static final By DOWNLOAD_DIALOG =
            AppiumBy.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View");

    public static final By DOWNLOAD_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"DOWNLOAD\"]");

    public static final By CANCEL_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"CANCEL\"]");

    public static final By SNACKBAR_TEXT =
            AppiumBy.xpath("//android.widget.TextView[@content-desc=\"snackbar_een_text\"]");

    public static final By IN_PROGRESS_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"In progress\"]");

    // Brittle: positional XPath - no resource-id or content-desc available
    // NOTE: This locator is identical to FIRST_FOLDER_THREE_DOTS because the cancel icon
    // appears at the same position as the folder's 3-dots menu during folder downloads.
    // For video downloads inside a folder, the cancel icon position may differ.
    public static final By CANCEL_DOWNLOAD_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]");

    public static final By YES_CANCEL_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"YES, CANCEL\"]");
}
