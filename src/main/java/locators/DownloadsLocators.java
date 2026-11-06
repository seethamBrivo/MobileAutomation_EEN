package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

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

    public static final By DOWNLOADS_MENU_ITEM =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Downloads\"]");
    // endregion

    // region Tabs
    // Downloads screen-specific tabs (different scope from ArchiveLocators)
    public static final By FILES_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.TextView[@text=\"Files\"]");

    public static final By PROGRESS_TAB =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.TextView[@text=\"Progress\"]");
    // endregion

    // region Content
    // TODO: Ask app team for stable IDs - positional XPath, brittle
    public static final By FIRST_FOLDER =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]");

    // TODO: Ask app team for stable IDs - brittle: folder action icon at View[2]/View[1]/View[2]
    // This icon changes state: download icon initially, becomes cancel icon during active download
    public static final By FOLDER_ACTION_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]");

    // TODO: Ask app team for stable IDs - brittle: video action icon at View[1]/View[1]/View[2]
    // This icon changes state: download icon initially, becomes cancel icon during active download
    public static final By VIDEO_ACTION_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]");
    // endregion

    // region Dialog
    public static final By DOWNLOAD_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"DOWNLOAD\"]");

    public static final By CANCEL_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"CANCEL\"]");
    // endregion

    // region Progress
    // Progress bar displayed during download - text value changes dynamically (0.17, 0.47, etc.)
    // so we find it by widget type only, scoped to Downloads screen
    public static final By DOWNLOAD_PROGRESS_BAR =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.ProgressBar");
    // endregion
}
