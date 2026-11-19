package tests.ts_history;

import base.BaseTest;
import constants.TestDataConstants;
import java.time.LocalTime;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CamerasPage;
import pages.DevicesPage;
import pages.EventFilterPage;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;
import pages.VideoSearchPage;
import utils.DeviceUtils;

public class HistoryBrowserTest extends BaseTest {

    private LayoutsPage layoutsPage;
    private HistoryBrowserPage historyBrowserPage;
    private EventFilterPage eventFilterPage;
    private DevicesPage devicesPage;
    private CamerasPage camerasPage;
    private VideoSearchPage videoSearchPage;
    private boolean landscapeUsed;
    private boolean eventFilterUsed;

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenLayouts() {
        landscapeUsed = false;
        eventFilterUsed = false;
        layoutsPage = null;
        historyBrowserPage = null;
        eventFilterPage = null;
        devicesPage = null;
        camerasPage = null;
        videoSearchPage = null;
        layoutsPage = loginAndCreatePage(TestDataConstants.ENDUSER_CREDS, LayoutsPage::new);
        devicesPage = new DevicesPage(driver);
        camerasPage = new CamerasPage(driver);
        videoSearchPage = new VideoSearchPage(driver);
        historyBrowserPage = new HistoryBrowserPage(driver);
        eventFilterPage = new EventFilterPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.verifyCameraPreviewsLoaded();
    }

    @Test(description = "EEPD-TC-2606: POS-History Browser - Pinch on timeline to change timeline level "
            + "in History Browser in mobile VMS app",
          groups = {"regression", "history", "ios"})
    public void verifyPinchChangesTimelineLevel() {
        historyBrowserPage.openHistoryBrowserFromCameraPreview(simCameraName());
        historyBrowserPage.verifyDefaultTimelineLevel();
        historyBrowserPage.pinchInAndVerifyMaxTimelineLevel();
        historyBrowserPage.pinchOutAndVerifyMinTimelineLevel();
    }

    @Test(description = "EEPD-TC-2907: POS-History Browser - User is able to use Players/Controllers in mobile VMS app",
          groups = {"regression", "history", "ios"})
    public void verifyTimelineControlsMoveRedCursor() {
        historyBrowserPage.openLiveVideoOfCameraPreview(simCameraName());
        historyBrowserPage.openHistoryBrowserFromLiveVideo();
        historyBrowserPage.verifyTimelineControlsDisplayed();
        historyBrowserPage.requireEventsOnTimeline();
        historyBrowserPage.ensurePlaybackPaused();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousFrameAndVerifyCursorMoved();
        historyBrowserPage.clickNextFrameAndVerifyCursorMoved();
        historyBrowserPage.clickNextEventAndVerifyCursorMoved();
    }

    @Test(description = "EEPD-TC-2605: POS-History Browser - Navigate between previews using Previous/Next Frame "
            + "buttons in History Browser in mobile VMS app",
          groups = {"regression", "history", "ios"})
    public void verifyFrameButtonsNavigateBetweenMoments() {
        historyBrowserPage.openContextMenuOfCameraPreview(simCameraName());
        historyBrowserPage.openHistoryBrowserFromContextMenu();
        historyBrowserPage.verifyTimelineAndControlsDisplayedInPortrait();
        historyBrowserPage.requireEventsOnTimeline();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.tapPreviousFrameAndVerifyMoment();
        historyBrowserPage.tapNextFrameAndVerifyMoment();
        historyBrowserPage.verifyPlaybackPaused();
        historyBrowserPage.longPressPreviousFrameAndVerifyFastMove();
        historyBrowserPage.longPressNextFrameAndVerifyFastMove();
        historyBrowserPage.verifyPlaybackPaused();
        landscapeUsed = true;
        historyBrowserPage.rotateToLandscapeAndVerifyHistoryBrowser();
        historyBrowserPage.verifyTimelineShowsByDefaultAndClosesInLandscape();
        historyBrowserPage.showTimelineAndVerifyControlsInLandscape();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.tapPreviousFrameAndVerifyMoment();
        historyBrowserPage.tapNextFrameAndVerifyMoment();
        historyBrowserPage.verifyPlaybackPaused();
        historyBrowserPage.longPressPreviousFrameAndVerifyFastMove();
        historyBrowserPage.longPressNextFrameAndVerifyFastMove();
        historyBrowserPage.verifyPlaybackPaused();
    }

    @Test(description = "EEPD-TC-29058: POS-History Browser - Filter the events using multiselection in History "
            + "Browser in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyFilterEventsUsingMultiselection() {
        historyBrowserPage.openContextMenuOfCameraPreview(simCameraName());
        historyBrowserPage.openHistoryBrowserFromContextMenu();
        List<String> onTimeline = eventFilterPage.eventTypesOnTimeline();
        historyBrowserPage.verifyFilterButtonWithoutIndication();
        historyBrowserPage.verifyEventCardsMatchTimelineRange();
        eventFilterUsed = true;
        eventFilterPage.openEventFilter();
        List<String> listedTypes = eventFilterPage.verifyEventFilterListDefaultsAndReadTypes();
        List<String> eventTypes = eventFilterPage.chooseEventTypes(onTimeline, listedTypes, 2);
        eventFilterPage.multiSelectEventTypes(eventTypes, true);
        eventFilterPage.confirmEventFilterAndVerifyTimeline(eventTypes);
        eventFilterPage.discardEventFilterChangesAndVerifyUnchanged(eventTypes, true);
        eventFilterPage.selectAllEventTypesAndVerify();
        eventFilterPage.deselectAllEventTypesAndVerify();
        eventFilterPage.closeEventFilterWithX();
        landscapeUsed = true;
        historyBrowserPage.rotateToLandscapeAndVerifyHistoryBrowser();
        historyBrowserPage.verifyFilterButtonWithoutIndication();
        historyBrowserPage.verifyTimelineShowsByDefaultAndClosesInLandscape();
        historyBrowserPage.showTimelineAndVerifyControlsInLandscape();
        historyBrowserPage.verifyEventCardsMatchTimelineRange();
        eventFilterPage.openEventFilter();
        eventFilterPage.multiSelectEventTypes(eventTypes, false);
        eventFilterPage.confirmEventFilterAndVerifyTimeline(eventTypes);
        eventFilterPage.discardEventFilterChangesAndVerifyUnchanged(eventTypes, false);
        eventFilterPage.selectAllEventTypesAndVerify();
        eventFilterPage.deselectAllEventTypesAndVerify();
        eventFilterPage.closeEventFilterWithX();
    }

    @Test(description = "EEPD-TC-28549: POS-History Browser - Navigate to the current moment in History Browser "
            + "using Long Press Menu in Dashboard in mobile VMS app",
          groups = {"regression", "history", "ios"})
    public void verifyHistoryBrowserOpensFromLongPressMenuInCameras() {
        devicesPage.navigateToDashboard();
        devicesPage.verifyAllDevicesSectionIsDisplayed();
        camerasPage.openCamerasList();
        camerasPage.longPressCameraNamedAndVerifyContextMenu(simCameraName());
        String cameraName = camerasPage.readCameraNameInContextMenu();
        LocalTime frameTime = camerasPage.readFrameTimeInContextMenu();
        historyBrowserPage.openHistoryBrowserFromContextMenu();
        historyBrowserPage.verifyOpenedAtCurrentMoment(frameTime);
        verifyPausedHistoryBrowserInPortraitAndLandscape(cameraName);
    }

    @Test(description = "EEPD-TC-5497: POS-History Browser - Navigate to the specific moment in the History Browser "
            + "based on the event selected in Video Search in mobile VMS app",
          groups = {"regression", "history", "ios"})
    public void verifyHistoryBrowserOpensAtEventSelectedInVideoSearch() {
        videoSearchPage.openVideoSearch();
        VideoSearchPage.OpenedEvent event = videoSearchPage.openFirstPersonEvent();
        videoSearchPage.openHistoryBrowserFromEvent();
        historyBrowserPage.verifyOpenedAtEventMoment(event.dateTime());
        verifyPausedHistoryBrowserInPortraitAndLandscape(event.cameraName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
            if (driver != null && historyBrowserPage != null) {
                restore("multi-window setting", historyBrowserPage::resetMultiWindowSetting);
            }
            if (driver != null && landscapeUsed) {
                restore("portrait orientation", () -> DeviceUtils.rotateToPortrait(driver));
            }
            if (driver != null && eventFilterUsed && eventFilterPage != null) {
                restore("default event filter", eventFilterPage::restoreDefaultEventFilter);
            }
            if (driver != null && historyBrowserPage != null) {
                restore("live video", historyBrowserPage::returnToLiveVideoIfInHistoryBrowser);
            }
        } finally {
            super.tearDown(testResult);
        }
    }

    /** The portrait screen, then the rotation, then the landscape screen: the last steps of both opening tests. */
    private void verifyPausedHistoryBrowserInPortraitAndLandscape(String cameraName) {
        historyBrowserPage.verifyPausedHistoryBrowserElementsInPortrait(cameraName);
        landscapeUsed = true;
        historyBrowserPage.rotateToLandscapeAndVerifyHistoryBrowser();
        historyBrowserPage.verifyPausedHistoryBrowserElementsInLandscape(cameraName);
    }

    /** One cleanup step: a failure is logged and the next step still runs. */
    private void restore(String what, Runnable step) {
        try {
            step.run();
        } catch (Throwable t) {
            logger.warn("Cleanup: could not restore the {}: {}", what, t.toString());
        }
    }
}
