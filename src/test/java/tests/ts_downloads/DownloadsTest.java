package tests.ts_downloads;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DownloadsPage;

public class DownloadsTest extends BaseTest {

    private DownloadsPage downloadsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupDownloadsTest() {
        loginAs(TestDataConstants.TS02_CREDS);
        downloadsPage = new DownloadsPage(driver);
    }

    @Test(description = "EEPD-TC-13330: POS-Downloads - downloading a folder in mobile app",
          groups = {"regression", "downloads"})
    public void verifyDownloadsFolderDownload() {
        downloadsPage.verifyCompleteFolderDownloadFlow();
        downloadsPage.verifyFolderDownloadCancellation();
    }

    @Test(description = "EEPD-TC-13207: POS-Downloads - downloading a video - Full video type in mobile app",
          groups = {"regression", "downloads"})
    public void verifyDownloadsVideoDownload() {
        downloadsPage.verifyCompleteVideoDownloadFlow();
        downloadsPage.verifyVideoDownloadCancellation();
    }
}
