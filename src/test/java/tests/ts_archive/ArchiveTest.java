package tests.ts_archive;

import base.BaseTest;
import constants.TestDataConstants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ArchivePage;
import utils.JsonDataReader;

public class ArchiveTest extends BaseTest {

    private ArchivePage archivePage;

    @BeforeMethod(alwaysRun = true)
    public void setupArchiveTest() {
        loginAs(TestDataConstants.ENDUSER_CREDS);
        archivePage = new ArchivePage(driver);
    }

    @Test(description = "EEPD-TC-19978: POS-Archive - Searching for files in mobile app",
          groups = {"regression", "archive", "search"})
    public void verifyArchiveFileSearch() {
        String validSearchTerm = JsonDataReader.getArchiveSearchValue(TestDataConstants.ARCHIVE_VALID_SEARCH_TERM);
        String noResultsSearchTerm = JsonDataReader.getArchiveSearchValue(TestDataConstants.ARCHIVE_NO_RESULTS_SEARCH_TERM);
        archivePage.verifyCompleteArchiveSearchFlow(validSearchTerm, noResultsSearchTerm);
    }

    @Test(description = "EEPD-TC-19818: POS-Archive - downloading a folder in mobile app",
          groups = {"regression", "archive", "download"})
    public void verifyArchiveFolderDownload() {
        archivePage.verifyCompleteFolderDownloadFlow();
        archivePage.verifyFolderDownloadCancellation();
    }

    @Test(description = "EEPD-TC-19385: POS-Archive - downloading a video - video type in mobile app",
          groups = {"regression", "archive", "download"})
    public void verifyArchiveVideoDownload() {
        archivePage.verifyCompleteVideoDownloadFlow();
        archivePage.verifyVideoDownloadCancellation();
    }
}
