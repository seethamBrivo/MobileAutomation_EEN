package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.PlatformLocator;
import utils.XPathUtils;

/**
 * Locators specific to Downloads feature.
 * Shared locators (IN_PROGRESS_STATUS, YES_CANCEL_BUTTON, SNACKBAR_TEXT) are in ArchiveLocators and CommonLocators.
 * FILES_TAB and PROGRESS_TAB are kept here because Downloads screen has different scoping than Archive.
 */
public final class DownloadsLocators {

    private DownloadsLocators() {}

    // region Navigation
    public static final By FILES_MENU_ITEM =
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Files']");

    // iOS lists Downloads directly on the More screen, without the Files section of Android.
    public static final By DOWNLOADS_MENU_ITEM = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Downloads\"]"), // android
            AppiumBy.accessibilityId("downloads_button")); // ios
    // endregion

    // region Tabs
    // Downloads screen-specific tabs (different scope from ArchiveLocators)
    public static final By FILES_TAB = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.TextView[@text=\"Files\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Files'")); // ios

    public static final By PROGRESS_TAB = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.TextView[@text=\"Progress\"]"), // android
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Progress'")); // ios
    // endregion

    // region Content
    // Rows are addressed by their 0-based position. TODO: Ask app team for stable IDs on Android - positional XPath.
    private static final String ANDROID_LIST = "//androidx.compose.ui.platform.ComposeView"
            + "[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View"
            + "/android.view.View/android.view.View";

    /** The folder row, which opens the folder. */
    public static By folderRow(int row) {
        return PlatformLocator.get(
                AppiumBy.xpath(ANDROID_LIST + "[2]/android.view.View[" + (row + 1) + "]"), // android
                rowAction(row, "secondary_action")); // ios
    }

    /** The download icon of a folder row. On Android it turns into the cancel icon while the folder downloads. */
    public static By folderDownloadIcon(int row) {
        return PlatformLocator.get(
                AppiumBy.xpath(ANDROID_LIST + "[2]/android.view.View[" + (row + 1) + "]/android.view.View[2]"), // android
                rowAction(row, "primary_action")); // ios
    }

    /** The download icon of a video row inside a folder. On Android it turns into the cancel icon. */
    public static By videoDownloadIcon(int row) {
        return PlatformLocator.get(
                AppiumBy.xpath(ANDROID_LIST + "[1]/android.view.View[" + (row + 1) + "]/android.view.View[2]"), // android
                rowAction(row, "primary_action")); // ios
    }

    public static By folderCancelIcon(int row) {
        return PlatformLocator.isIos() ? rowAction(row, "cancel_action") : folderDownloadIcon(row);
    }

    public static By videoCancelIcon(int row) {
        return PlatformLocator.isIos() ? rowAction(row, "cancel_action") : videoDownloadIcon(row);
    }

    /** The size line of every video in an open folder, for example "46.53 MB \u2022 Expires 2026-11-26 06:03". */
    public static final By SIZE_LINES = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Expires')]"), // android
            AppiumBy.iOSNsPredicateString("name ENDSWITH '_downloads_description'")); // ios

    public static final By EMPTY_FOLDER_TEXT = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text='Downloads is empty']"), // android
            AppiumBy.accessibilityId("Downloads is empty")); // ios
    // endregion

    // region Dialog
    public static final By DOWNLOAD_BUTTON_DIALOG = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"DOWNLOAD\"]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert//XCUIElementTypeButton[@name='Download']")); // ios

    public static final By CANCEL_BUTTON_DIALOG = PlatformLocator.get(
            AppiumBy.xpath("//android.widget.TextView[@text=\"CANCEL\"]"), // android
            AppiumBy.xpath("//XCUIElementTypeAlert//XCUIElementTypeButton[@name='Cancel']")); // ios
    // endregion

    // region Progress
    // Progress bar displayed during download - text value changes dynamically (0.17, 0.47, etc.)
    // so we find it by widget type only, scoped to Downloads screen
    public static final By DOWNLOAD_PROGRESS_BAR = PlatformLocator.get(
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.ProgressBar"), // android
            AppiumBy.iOSNsPredicateString("name ENDSWITH '_downloads_progress_text'")); // ios: the row's "11.80 %" next to the bar
    // endregion

    // region Rows
    /** The row of a file or folder, found by its name. */
    public static By rowByFileName(String fileName) {
        if (PlatformLocator.isIos()) {
            return AppiumBy.iOSNsPredicateString("name ENDSWITH '_downloads_title' AND label CONTAINS[c] "
                    + XPathUtils.xpathLiteral(fileName));
        }
        return AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]"
                + "//android.widget.TextView[contains(@text, " + XPathUtils.xpathLiteral(fileName) + ")]");
    }

    /** iOS: any cancel icon on the screen, used by the cleanup. */
    public static final By ANY_CANCEL_ICON =
            AppiumBy.iOSNsPredicateString("name ENDSWITH '_downloads_cancel_action'");

    private static By rowAction(int row, String part) {
        return AppiumBy.accessibilityId("cell_at_" + row + "_downloads_" + part);
    }

    /** iOS: the back button of a folder screen. */
    public static final By FOLDER_BACK_BUTTON = AppiumBy.accessibilityId("BackButton");
    // endregion
}
