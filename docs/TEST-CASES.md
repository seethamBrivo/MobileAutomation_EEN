# Test Cases

Tests live in `src/test/java/tests/ts_<area>/`, one package per functional area.

| Test ID      |   Test Class | Description |
|--------------|--------------|-------------|
| (none) | ts_login.LoginTest | Sign-in smoke test, iOS suite only (`testng-ios.xml`) |
| EEPD-TC-4516 | ts_dashboard.ResellerDashboardTest | View Dashboard for Reseller |
| EEPD-TC-40220 | ts_dashboard.ResellerDashboardTest | Verify account creation for Admin / Account-access users |
| EEPD-TC-4170 | ts_users.AddUserTest | Adding a new user |
| EEPD-TC-11276 | ts_bridges.BridgesTest | Add bridge (incl. duplicate attach-ID conflict) |
| EEPD-TC-8890 | ts_bridges.BridgesTest | Delete bridge |
| EEPD-TC-16502 | ts_bridges.BridgesTest | Bridges list under Dashboard |
| EEPD-TC-15483 | ts_bridges.BridgesTest | Filter bridges by search term |
| EEPD-TC-12354 | ts_bridges.BridgesTest | Bridge context menu |
| EEPD-TC-8881 | ts_bridges.BridgesTest | Rename bridge and check status |
| EEPD-TC-8898 | ts_bridges.BridgeRestartTest | Restart bridge (reseller login) |
| EEPD-TC-53382 | ts_bridges.BridgeCameraSearchFilterTest | Filter respective bridge cameras by search term |
| EEPD-TC-15581 | ts_cameras.CameraSearchFilterTest | Filter cameras by search term under Cameras screen |
| EEPD-TC-12355 | ts_cameras.CameraContextMenuTest | Display context menu after long press on camera |
| EEPD-TC-9005 | ts_layouts.LayoutsTest | Add new layout in mobile VMS app |
| EEPD-TC-9163 | ts_layouts.LayoutsTest | Show camera previews in Layouts/Tags sections |
| EEPD-TC-2606 | ts_history.HistoryBrowserTest | Pinch on timeline to change timeline level in History Browser (first camera on All cameras must be online) |
| EEPD-TC-2907 | ts_history.HistoryBrowserTest | Use Players/Controllers in History Browser (fast-forward step not automated; first camera needs at least 2 events) |
| EEPD-TC-2605 | ts_history.HistoryBrowserTest | Navigate between previews using Previous/Next Frame buttons in History Browser (steps 1-12; the "button becomes disabled" steps 6 and 11 are not automated because the app shows no disabled state; needs allowAdbShell=true for local rotation) |
| EEPD-TC-28549 | ts_history.HistoryBrowserTest | Open History Browser from the long press menu in Dashboard > Cameras (steps 1-6; the blurred background of the menu is not automated; the filter button's enabled state is asserted only when an event card is shown; needs an online camera and allowAdbShell=true for local rotation) |
| EEPD-TC-5497 | ts_history.HistoryBrowserTest | Open History Browser at the event selected in Video Search (steps 1-5; opens the first Person card, skips when there is none; the event's date-time and the cursor must differ by a time zone gap (a whole number of 15 minutes, at most 26 hours), because Video Search shows the account's time zone and the History Browser the device's; the tags are asserted as present, not by value; needs allowAdbShell=true for local rotation) |
| EEPD-TC-29058 | ts_history.HistoryBrowserTest | Filter the events using multiselection in History Browser (steps 1-18; the default selection (the app selects every type, the test case says Motion only) is logged, not asserted; the one-card-per-period rule is not automated; needs allowAdbShell=true for local rotation and at least one event on the first camera's timeline) |
| EEPD-TC-37646 | ts_vsp_events.VSPEventsTest | VSP Events - Card display, pagination, and orientation testing |
| EEPD-TC-37714 | ts_vsp_events.VSPEventsTest | VSP Events - More info detail page verification with 17 fields |
| EEPD-TC-3920 | ts_my_profile.MyProfileTest | POS-My Profile - setting 'Time zone' in VMS mobile app |
| EEPD-TC-36684 | ts_live.LiveViewTest | POS-Live Stream - Mechanical Zoom of Auto focus functionality of live stream |
| EEPD-TC-36681 | ts_live.LiveViewTest | POS-Live Stream - Mechanical Zoom of Zoom In/out functionality of live stream |
| EEPD-TC-19978 | ts_archive.ArchiveTest | POS-Archive - Searching for files in mobile app |
| EEPD-TC-19818 | ts_archive.ArchiveTest | POS-Archive - downloading a folder in mobile app |
| EEPD-TC-19385 | ts_archive.ArchiveTest | POS-Archive - downloading a video - video type in mobile app |
| EEPD-TC-13330 | ts_downloads.DownloadsTest | POS-Downloads - downloading a folder in mobile app |
| EEPD-TC-13207 | ts_downloads.DownloadsTest | POS-Downloads - downloading a video - Full video type in mobile app |
| EEPD-TC-23628 | ts_history.HistoryBrowserExportTest | POS-Exports/History Browser - requesting a video export to Archive with default values in VMS mobile app |
| EEPD-TC-23629 | ts_history.HistoryBrowserExportTest | POS-Exports/History Browser - requesting a video export to Downloads with default values in VMS mobile app |
