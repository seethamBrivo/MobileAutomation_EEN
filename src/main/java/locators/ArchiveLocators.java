package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

public final class ArchiveLocators {

    private ArchiveLocators() {}

    public static final By FILES_MENU_ITEM =
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Files']");

    public static final By ARCHIVE_MENU_ITEM =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Archive']"), // android
            AppiumBy.accessibilityId("archive_button")); // ios

    public static final By FILES_TAB =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Files\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Files'")); // ios

    public static final By PROGRESS_TAB =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Progress\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Progress'")); // ios

    // Brittle: positional XPath - no resource-id or content-desc available for filter icon
    // Mobile team should add unique identifier to improve test stability
    // NOTE: Different indices for Files tab vs Progress tab!
    public static final By FILTER_ICON_FILES_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[4]/android.view.View[2]/android.view.View");

    public static final By FILTER_ICON_PROGRESS_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[2]/android.view.View");

    public static final By SEARCH_BAR =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[@text=\"Filter for files or folders\"]"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios

    // EditText for entering text (not the TextView placeholder above)
    public static final By SEARCH_INPUT =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.EditText"), // android
            AppiumBy.accessibilityId("navigationBar_searchField")); // ios

    // Back arrow button - position differs based on search results
    public static final By BACK_ARROW_BUTTON_WITH_RESULTS =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[3]/android.view.View[1]/android.view.View"), // android
            AppiumBy.accessibilityId("Close")); // ios

    public static final By BACK_ARROW_BUTTON_EMPTY_STATE =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View"), // android
            AppiumBy.accessibilityId("Close")); // ios

    // Brittle: positional XPath - no resource-id or content-desc available
    public static final By THREE_DOTS_MENU =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]"), // android
            AppiumBy.accessibilityId("ic_contextMenu")); // ios

    public static By searchResultByFileName(String fileName) {
        if (PlatformLocator.isIos()) {
            return AppiumBy.iOSNsPredicateString("name BEGINSWITH 'cell_at_' AND label CONTAINS[c] "
                    + XPathUtils.xpathLiteral(fileName));
        }
        return AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]//android.widget.TextView[contains(@text, " +
            XPathUtils.xpathLiteral(fileName) + ")]");
    }

    public static final By FILE_ITEM =
            PlatformLocator.get(
            AppiumBy.id("com.een.vms.android:id/file_item"), // android
            AppiumBy.iOSNsPredicateString("name ENDSWITH '_archives_title'")); // ios

    public static final By EMPTY_STATE =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"No results found\"]"), // android
            AppiumBy.accessibilityId("No results found")); // ios

    // iOS (FIRST_FOLDER, FIRST_FOLDER_THREE_DOTS, FIRST_VIDEO_THREE_DOTS): a row is told apart by the icon it holds (folderIcon for a folder, ic_play for a video), not by its name
    // or position, so the locators keep working when the account's folders change.

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_FOLDER_THREE_DOTS =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeCell[$type == 'XCUIElementTypeImage' AND name == 'folderIcon'$][1]/**/XCUIElementTypeButton[`name == 'ic_contextMenu'`][1]")); // ios

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_FOLDER =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeCell[$type == 'XCUIElementTypeImage' AND name == 'folderIcon'$][1]")); // ios

    // Brittle: positional XPath - no resource-id or content-desc available
    // Mobile team should add unique identifier to improve test stability
    public static final By FIRST_VIDEO_THREE_DOTS =
            PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"ArchiveMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View/android.view.View[2]"), // android
            AppiumBy.iOSClassChain("**/XCUIElementTypeCell[$type == 'XCUIElementTypeImage' AND name == 'ic_play'$][1]/**/XCUIElementTypeButton[`name == 'ic_contextMenu'`][1]")); // ios

    public static final By DETAILS_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Details\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Details'")); // ios

    public static final By DOWNLOAD_OPTION =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Download\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Download'")); // ios

    public static final By DOWNLOAD_BUTTON_DIALOG =
            PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"DOWNLOAD\"]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert//XCUIElementTypeButton[@name='Download']")); // ios

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

    // iOS only: the first download asks for access to the photo library, because iOS saves downloads to Photos.
    public static final By ALLOW_PHOTO_LIBRARY_ACCESS_BUTTON = AppiumBy.accessibilityId("Allow Full Access");

    // iOS only: the navigation bar's back button on a folder screen.
    public static final By FOLDER_BACK_BUTTON = AppiumBy.accessibilityId("BackButton");
}
