package tests.ts_history;

import base.BaseTest;
import constants.TestDataConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;

/**
 * Test suite for History Browser Export functionality.
 * EEPD-TC-23628: Tests video export to Archive with default values, cancellation, and file name validation.
 * EEPD-TC-23629: Tests video export to Downloads with default values, cancellation, and file name validation.
 */
public class HistoryBrowserExportTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(HistoryBrowserExportTest.class);
    private LayoutsPage layoutsPage;
    private HistoryBrowserPage historyBrowserPage;

    @BeforeMethod(alwaysRun = true)
    public void setupExportTest() {
        layoutsPage = loginAndCreatePage(TestDataConstants.ENDUSER_CREDS, LayoutsPage::new);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.verifyCameraPreviewsLoaded();
        historyBrowserPage = new HistoryBrowserPage(driver);
        historyBrowserPage.openHistoryBrowserFromFirstCameraPreview();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null) {
            return;
        }
        try {
            if (historyBrowserPage != null) {
                // Navigate back multiple times to recover from any screen (Export, Progress, Folder picker)
                for (int i = 0; i < 3; i++) {
                    try {
                        driver.navigate().back();
                        Thread.sleep(500);
                    } catch (Exception ignored) {
                        break;
                    }
                }
                historyBrowserPage.returnToLiveVideoIfInHistoryBrowser();
            }
        } catch (Throwable e) {
            logger.warn("@AfterMethod: cleanup failed: {}", e.toString());
        }
    }

    @Test(description = "EEPD-TC-23628: POS-Exports/History Browser - requesting a video export"
                    + " to Archive with default values in VMS mobile app",
          groups = {"regression", "history_browser", "export"})
    public void verifyVideoExportToArchiveWithValidation() {
        historyBrowserPage.exportToArchiveWithDefaultSettings();
        historyBrowserPage.completeExportWithProgress();
        historyBrowserPage.verifyCancelExportFlow();
        historyBrowserPage.verifyFileNameValidation();
        historyBrowserPage.verifyDefaultFileNameExport();
    }

    @Test(description = "EEPD-TC-23629: POS-Exports/History Browser - requesting a video export"
                    + " to Downloads with default values in VMS mobile app",
          groups = {"regression", "history_browser", "export"})
    public void verifyVideoExportToDownloadsWithValidation() {
        historyBrowserPage.exportToDownloadsWithDefaultSettings();
        historyBrowserPage.completeExportWithProgress();
        historyBrowserPage.verifyCancelExportFlowForDownloads();
        historyBrowserPage.verifyFileNameValidationForDownloads();
        historyBrowserPage.verifyDefaultFileNameExportForDownloads();
    }
}
