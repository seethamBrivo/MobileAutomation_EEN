package locators;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public final class DownloadsLocators {

    private DownloadsLocators() {}

    public static final By FILES_MENU_ITEM =
            AppiumBy.xpath("//android.widget.ScrollView//android.widget.TextView[@text='Files']");

    public static final By DOWNLOADS_MENU_ITEM =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Downloads\"]");

    public static final By FILES_TAB =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Files\"]");

    public static final By PROGRESS_TAB =
            AppiumBy.xpath("//android.widget.TextView[@text=\"Progress\"]");

    // Brittle: positional XPath
    public static final By FIRST_FOLDER =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]");

    // Brittle: folder downloads at View[2]/View[1]/View[2]
    public static final By DOWNLOAD_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]");

    // Brittle: video downloads at View[1]/View[1]/View[2]
    public static final By VIDEO_DOWNLOAD_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]");

    public static final By DOWNLOAD_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"DOWNLOAD\"]");

    public static final By CANCEL_BUTTON_DIALOG =
            AppiumBy.xpath("//android.widget.TextView[@text=\"CANCEL\"]");

    public static final By SNACKBAR_TEXT =
            AppiumBy.accessibilityId("snackbar_een_text");

    public static final By IN_PROGRESS_STATUS =
            AppiumBy.xpath("//android.widget.TextView[@text=\"In progress\"]");

    // Progress bar displayed during download - text value changes dynamically (0.17, 0.47, etc.)
    // so we find it by widget type only, scoped to Downloads screen
    public static final By DOWNLOAD_PROGRESS_BAR =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]//android.widget.ProgressBar");

    // Cancel icons replace download icons at same position during active downloads
    public static final By CANCEL_DOWNLOAD_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[1]/android.view.View[2]");

    public static final By VIDEO_CANCEL_DOWNLOAD_ICON =
            AppiumBy.xpath("//androidx.compose.ui.platform.ComposeView[@content-desc=\"DownloadsMainScreen\"]/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]");

    public static final By YES_CANCEL_BUTTON =
            AppiumBy.xpath("//android.widget.TextView[@text=\"YES, CANCEL\"]");

    // Scoped to notification area (status_bar_latest_event_content) to avoid matching app UI
    public static final By NOTIFICATION_DOWNLOAD_TITLE =
            AppiumBy.xpath("//android.widget.FrameLayout[@resource-id='android:id/status_bar_latest_event_content']//android.widget.TextView[contains(@text, 'Download') or contains(@text, 'download') or contains(@text, '.zip')]");
}
