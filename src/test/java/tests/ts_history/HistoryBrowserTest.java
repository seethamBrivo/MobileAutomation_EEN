package tests.ts_history;

import base.BaseTest;
import constants.TestDataConstants;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.EventFilterPage;
import pages.HistoryBrowserPage;
import pages.LayoutsPage;
import utils.DeviceUtils;

public class HistoryBrowserTest extends BaseTest {

    private LayoutsPage layoutsPage;
    private HistoryBrowserPage historyBrowserPage;
    private EventFilterPage eventFilterPage;
    private boolean landscapeUsed;
    private boolean eventFilterUsed;

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenLayouts() {
        landscapeUsed = false;
        eventFilterUsed = false;
        layoutsPage = null;
        historyBrowserPage = null;
        eventFilterPage = null;
        loginAs(TestDataConstants.ENDUSER_CREDS);
        layoutsPage = new LayoutsPage(driver);
        historyBrowserPage = new HistoryBrowserPage(driver);
        eventFilterPage = new EventFilterPage(driver);
        layoutsPage.verifyLayoutsScreenDisplayed();
        layoutsPage.verifyCameraPreviewsLoaded();
    }

    @Test(description = "EEPD-TC-2606: POS-History Browser - Pinch on timeline to change timeline level "
            + "in History Browser in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyPinchChangesTimelineLevel() {
        historyBrowserPage.openHistoryBrowserFromFirstCameraPreview();
        historyBrowserPage.verifyDefaultTimelineLevel();
        historyBrowserPage.pinchInAndVerifyMaxTimelineLevel();
        historyBrowserPage.pinchOutAndVerifyMinTimelineLevel();
    }

    @Test(description = "EEPD-TC-2907: POS-History Browser - User is able to use Players/Controllers in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyTimelineControlsMoveRedCursor() {
        historyBrowserPage.openLiveVideoOfFirstCameraPreview();
        historyBrowserPage.openHistoryBrowserFromLiveVideo();
        historyBrowserPage.verifyTimelineControlsDisplayed();
        historyBrowserPage.requireEventsOnTimeline();
        historyBrowserPage.ensurePlaybackPaused();
        historyBrowserPage.clickPreviousFrameAndVerifyCursorMoved();
        historyBrowserPage.clickNextFrameAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickPreviousEventAndVerifyCursorMoved();
        historyBrowserPage.clickNextEventAndVerifyCursorMoved();
    }

    @Test(description = "EEPD-TC-2605: POS-History Browser - Navigate between previews using Previous/Next Frame "
            + "buttons in History Browser in mobile VMS app",
          groups = {"regression", "history"})
    public void verifyFrameButtonsNavigateBetweenMoments() {
        historyBrowserPage.openContextMenuOfFirstCameraPreview();
        historyBrowserPage.openHistoryBrowserFromContextMenu();
        historyBrowserPage.verifyTimelineAndControlsDisplayedInPortrait();
        historyBrowserPage.tapPreviousFrameAndVerifyMoment();
        historyBrowserPage.tapNextFrameAndVerifyMoment();
        historyBrowserPage.verifyPlaybackPaused();
        historyBrowserPage.longPressPreviousFrameAndVerifyFastMove();
        historyBrowserPage.longPressNextFrameAndVerifyFastMove();
        historyBrowserPage.verifyPlaybackPaused();
        landscapeUsed = true;
        historyBrowserPage.rotateToLandscapeAndVerifyHistoryBrowser();
        historyBrowserPage.verifyTimelineHiddenInLandscape();
        historyBrowserPage.showTimelineAndVerifyControlsInLandscape();
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
        historyBrowserPage.openContextMenuOfFirstCameraPreview();
        historyBrowserPage.openHistoryBrowserFromContextMenu();
        historyBrowserPage.verifyFilterButtonWithoutIndication();
        historyBrowserPage.verifyEventCardsMatchTimelineRange();
        List<String> onTimeline = eventFilterPage.eventTypesOnTimeline();
        eventFilterUsed = true;
        eventFilterPage.openEventFilter();
        List<String> listedTypes = eventFilterPage.verifyEventFilterListDefaultsAndReadTypes();
        List<String> eventTypes = eventFilterPage.chooseEventTypes(onTimeline, listedTypes, 2);
        List<String> otherTypes = eventFilterPage.unselectedEventTypes(onTimeline, eventTypes);
        eventFilterPage.multiSelectEventTypes(eventTypes, true);
        eventFilterPage.confirmEventFilterAndVerifyTimeline(eventTypes, otherTypes);
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
        eventFilterPage.confirmEventFilterAndVerifyTimeline(eventTypes, otherTypes);
        eventFilterPage.discardEventFilterChangesAndVerifyUnchanged(eventTypes, false);
        eventFilterPage.selectAllEventTypesAndVerify();
        eventFilterPage.deselectAllEventTypesAndVerify();
        eventFilterPage.closeEventFilterWithX();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
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

    /** One cleanup step: a failure is logged and the next step still runs. */
    private void restore(String what, Runnable step) {
        try {
            step.run();
        } catch (Throwable t) {
            logger.warn("Cleanup: could not restore the {}: {}", what, t.toString());
        }
    }
}
