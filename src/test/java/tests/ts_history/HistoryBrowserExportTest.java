package tests.ts_history;

import base.BaseTest;
import constants.TestDataConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ArchivePage;
import pages.DevicesPage;
import pages.DownloadsPage;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;

/**
 * Test suite for History Browser Export functionality.
 * EEPD-TC-23628: Tests video export to Archive with default values, cancellation, and file name validation.
 * EEPD-TC-23629: Tests video export to Downloads with default values, cancellation, and file name validation.
 */
public class HistoryBrowserExportTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(HistoryBrowserExportTest.class);
    private HistoryBrowserPage historyBrowserPage;
    private ArchivePage archivePage;
    private DownloadsPage downloadsPage;
    private DevicesPage devicesPage;

    @BeforeMethod(alwaysRun = true)
    public void setupExportTest() {
        LayoutsPage layoutsPage = loginAndCreatePage(TestDataConstants.ENDUSER_CREDS, LayoutsPage::new);
        layoutsPage.verifyLayoutsScreenDisplayed();
        historyBrowserPage = new HistoryBrowserPage(driver);
        archivePage = new ArchivePage(driver);
        downloadsPage = new DownloadsPage(driver);
        devicesPage = new DevicesPage(driver);
        historyBrowserPage.openHistoryBrowserFromFirstCameraPreview();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver == null) {
            return;
        }
        try {
            if (historyBrowserPage != null) {
                historyBrowserPage.recoverFromExportFlow();
                historyBrowserPage.returnToLiveVideoIfInHistoryBrowser();
            }
        } catch (Throwable e) {
            logger.warn("@AfterMethod: cleanup failed: {}", e.toString());
        }
        // The tests end in the Archive or Downloads list, which the steps above do not leave.
        try {
            devicesPage.recoverToDashboard();
        } catch (Throwable e) {
            logger.warn("@AfterMethod: recovering to the dashboard failed: {}", e.toString());
        }
    }

    @Test(description = "EEPD-TC-23628: POS-Exports/History Browser - requesting a video export"
                    + " to Archive with default values in VMS mobile app",
          groups = {"regression", "history_browser", "export", "ios"})
    public void test01_videoExportToArchiveWithValidation() {
        historyBrowserPage.exportToArchiveWithDefaultSettings();
        historyBrowserPage.completeExportWithProgress();
        historyBrowserPage.verifyCancelExportFlow();
        historyBrowserPage.verifyFileNameValidation();
        String exportedFileName = historyBrowserPage.verifyDefaultFileNameExport();
        historyBrowserPage.leaveHistoryBrowser();
        archivePage.verifyExportedFileListed(exportedFileName);
    }

    @Test(description = "EEPD-TC-23629: POS-Exports/History Browser - requesting a video export"
                    + " to Downloads with default values in VMS mobile app",
          groups = {"regression", "history_browser", "export", "ios"})
    public void test02_videoExportToDownloadsWithValidation() {
        historyBrowserPage.exportToDownloadsWithDefaultSettings();
        historyBrowserPage.completeExportWithProgress();
        historyBrowserPage.verifyCancelExportFlowForDownloads();
        historyBrowserPage.verifyFileNameValidation();
        String exportedFileName = historyBrowserPage.verifyDefaultFileNameExportForDownloads();
        historyBrowserPage.leaveHistoryBrowser();
        downloadsPage.verifyExportedFileListed(exportedFileName);
    }
}
