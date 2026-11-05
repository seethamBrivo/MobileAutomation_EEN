package constants;

/** The device-type tiles on the Dashboard's All devices screen. */
public enum DashboardTab {
    BRIDGES("Bridges"),
    CAMERAS("Cameras"),
    SPEAKERS("Speakers"),
    SWITCHES("Switches");

    private final String displayName;

    DashboardTab(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
