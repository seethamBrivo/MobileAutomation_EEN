package locators;

import constants.UiTextConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import utils.XPathUtils;

/** Locators for the history browser's event filter sheet. */
public final class EventFilterLocators {

    private EventFilterLocators() {}

    public static final By EVENT_FILTERS_TITLE =
            AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/title'][@text="
                    + XPathUtils.xpathLiteral(UiTextConstants.EVENT_FILTERS_TITLE) + "]");

    public static final By FILTER_CLOSE_BUTTON =
            AppiumBy.id("com.een.vms.android:id/cross_icon");

    public static final By FILTER_DONE_BUTTON =
            AppiumBy.accessibilityId("done_button_button");

    // The sheet has an OVERLAYS and a TIMELINE EVENTS section, each with its own Select/Deselect button; the rows
    // are flat siblings of the section header, so everything below is addressed from the timeline events header.
    private static final String TIMELINE_EVENTS_SECTION =
            "//*[@resource-id='com.een.vms.android:id/header'][@text="
                    + XPathUtils.xpathLiteral(UiTextConstants.TIMELINE_EVENTS_HEADER) + "]";

    public static final By EVENT_TYPES_SELECT_ALL =
            AppiumBy.xpath(TIMELINE_EVENTS_SECTION
                    + "/following-sibling::*[@resource-id='com.een.vms.android:id/selectAll'][1]");

    public static By eventTypeCheckbox(String eventType) {
        return AppiumBy.xpath("//*[@resource-id='com.een.vms.android:id/title'][@text="
                + XPathUtils.xpathLiteral(eventType)
                + "]/following-sibling::*[@resource-id='com.een.vms.android:id/checkBox'][1]");
    }
}
