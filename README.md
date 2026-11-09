# Mobile Automation Framework - EEN VMS

Mobile automation testing framework for EEN (Eagle Eye Networks) Video Management System using Appium, TestNG, and Java.

## Tech Stack

- **Java**: 17+
- **Build Tool**: Maven 3.8+
- **Test Framework**: TestNG 7.12.0
- **Mobile Automation**: Appium 3.x
- **Reporting**: Extent Reports 5.1.2
- **Design Pattern**: Page Object Model (POM)

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 17+ | Development & Execution |
| Maven | 3.8+ | Build & Dependency Management |
| Node.js | 22+ | Required by Appium |
| Appium | 3.x | Mobile automation server |
| Android SDK / ADB | Latest | Android testing |
| Xcode | Latest | iOS testing (macOS only) |

### Install Appium and Drivers

```bash
npm install -g appium
appium driver install uiautomator2
appium driver install xcuitest
```

## Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd MobileAutomation_EEN
```

### 2. Configure Properties

Copy the example configuration files and update with your values:

```bash
# Copy config files
cp src/main/resources/config.properties.example src/main/resources/config.properties
cp src/main/resources/android.properties.example src/main/resources/android.properties
cp src/main/resources/ios.properties.example src/main/resources/ios.properties
cp src/test/resources/testData.json.example src/test/resources/testData.json
```

**Update the following in each file:**

- `config.properties` - Appium URL, BrowserStack credentials, login credentials
- `android.properties` - Device name, UDID, app path, package/activity
- `ios.properties` - Device name, UDID, app path, bundle ID
- `testData.json` - Test user credentials and test data

### 3. Install Dependencies

```bash
mvn clean install -DskipTests
```

## Execution

### Start Appium Server

```bash
appium
# Or on custom port
appium --port 4724
```

### Run Tests

**All tests:**
```bash
mvn test -Dplatform=android
```

**Specific test class:**
```bash
mvn test -Dplatform=android -Dtest=BridgesTest
mvn test -Dplatform=android -Dtest=ResellerDashboardTest
mvn test -Dplatform=android -Dtest=AddUserTest
```

**With TestNG XML:**
```bash
mvn test -DsuiteXmlFile=testng.xml -Dplatform=android
```

**By test groups:**
```bash
mvn test -Dplatform=android -Dgroups=regression    # all regression tests
mvn test -Dplatform=android -Dgroups=layouts       # only layout tests
mvn test -Dplatform=android -Dgroups=users         # only user tests
mvn test -Dplatform=android -Dgroups=bridges       # only bridge tests
mvn test -Dplatform=android -Dgroups=cameras       # only camera tests
mvn test -Dplatform=android -Dgroups=dashboard     # only dashboard tests
mvn test -Dplatform=android -Dgroups=profile       # only profile tests
mvn test -Dplatform=android -Dgroups=vsp           # only VSP tests
mvn test -Dplatform=android -Dgroups=events        # only event-related tests
mvn test -Dplatform=android -Dgroups=live          # only live view tests
mvn test -Dplatform=android -Dgroups=archive       # only archive tests
mvn test -Dplatform=android -Dgroups=account_switching  # only account switching tests
```

**BrowserStack execution:**
```bash
mvn test -Dplatform=android -DexecutionProvider=browserstack -DbrowserstackBuildName="My Build"
```

### BrowserStack SDK (optional)

The default BrowserStack run builds its own capabilities and reports status itself. Adding the
BrowserStack SDK on top gives Test Reporting & Analytics (smart reporting, failure analysis, debugging)
and lets BrowserStack name builds/sessions and mark statuses.

```bash
export BROWSERSTACK_USERNAME=... BROWSERSTACK_ACCESS_KEY=... BROWSERSTACK_APP_URL=bs://...
mvn test -Dplatform=android -DexecutionProvider=browserstack -DbrowserstackSdk=true
```

- `-DbrowserstackSdk=true` activates the `browserstack-sdk` Maven profile (SDK dependency + `-javaagent`).
  Without it nothing changes and `browserstack.yml` is ignored.
- Config lives in `browserstack.yml` (device, `parallelsPerPlatform`, project/build names, observability).
  Credentials and the app URL come from the environment variables above, never from the file.
- In SDK mode `DriverManager` stops sending `bstack:options`; the SDK supplies them.
- In CI: Actions > CI > Run workflow > tick `use_sdk` (the push-to-main run stays on the default path).
- `parallelsPerPlatform: 1` keeps the run sequential. Do not combine `-DthreadCount` with a higher value.

### Retrying failed tests

Every `@Test` is re-run once if it fails (`listeners.RetryAnalyzer`, attached by
`listeners.RetryTransformer` in `testng.xml`). With `-DsessionScope=method` each attempt gets a fresh
Appium session; in the default class scope the retry reuses the class session (a new one is started
only if the old one is dead). A failed attempt keeps its screenshot in the report.

```bash
mvn test -Dplatform=android -DretryCount=2   # up to 2 extra attempts
mvn test -Dplatform=android -DretryCount=0   # disable retries
```

TestNG does not retry failed `@BeforeMethod` methods; login has its own retry in `LoginPage.loginAs`.
A test that only passes on retry is flaky: check the earlier attempt's session before ignoring it.

### Session scope: one session and login per class

By default (`-DsessionScope=class`, Android and iOS) a test class runs in **one Appium session**, and its
tests reuse the login:

- `BaseTest` opens the session in `@BeforeClass` and quits it in `@AfterClass`. The session name is the
  class name, and BrowserStack gets one pass/fail status for the session plus a `Start Class.test`
  annotation per test on its timeline.
- `loginAs(account)` signs in the first time. Later tests relaunch the app (`mobile: startActivity`,
  data kept) to get back to the landing screen and skip the login. If the login did not survive the
  relaunch it signs in again. Asking for another account logs out first; an unknown app state is wiped
  with `mobile: clearApp`. Cleanup of test data is unchanged (the `@AfterMethod` hooks).
- A dead Appium session is replaced at the start of the next test.
- `-DsessionScope=method` restores the old behaviour (new session, UI login and logout per test); use it
  to compare timings.
- iOS follows the same scope through the same `loginAs(account)`: it relaunches the app (`mobile: terminateApp` +
  `activateApp`) and reuses the login if the app comes back signed in. iOS has no UI logout, so a different
  account gets a new session.
- The app has no deep link to its dashboard (its manifest only declares `/camera_history/`,
  `/camera_history_status/` and `/notification/` links), so relaunching `MainActivity` is the way back.

### iOS tests

Android and iOS share the same locator, page and test classes. Each element in a locator class holds both
platforms' values, picked once by `PlatformLocator.get(android, ios)` from `-Dplatform`; pages only branch where the
iOS flow really differs (for example, aspect ratio is chosen from a list screen on iOS). `BaseTest.loginAs` and the
class-level session logic work for both.

```bash
mvn test -Dplatform=ios -DexecutionProvider=browserstack -Dsuite.file=testng-ios.xml
```

- `testng-ios.xml` runs the shared test classes, limited to the methods in the **`ios`** group. To run another
  test on iOS, add `"ios"` to its `groups` (after its screens have iOS locators); the Android suites are unchanged.
- Where iOS behaves differently, the shared page checks it and says why. `ArchiveTest` is the example: iOS saves
  downloads to Photos (the first one asks for photo-library access, which the test allows, and the message is
  "Saved to Photos"), cancelling a download works as on Android (the X on the row, a "Cancel download" alert, then
  "Download cancelled"), and the search field is always shown, so the "blank before searching" check is skipped.
- Upload the IPA with `-F "ios_app_groups_support=true"`, put its `bs://` id in `ios.properties`
  (`browserstackApp`), and keep `browserstackNetworkLogs=false` for iOS (see `ios.properties.example`).

BrowserStack debug logs, network logs and video are off by default because they slow sessions down:
set `browserstackDebug`, `browserstackNetworkLogs` and `browserstackVideo` to `true` (or tick
`diagnostics` when running the CI workflow) to diagnose a failure.

### CI on a pull request

A pull request (not a draft, not from a fork) runs only the tests it affects, Android then iOS, and nothing runs on
a merge to `main`. `scripts/select-tests.sh` reads the diff and writes `testng-pr.xml`: a changed or new test class
runs; a changed page or locator runs every test class that uses it; a change to shared code (`base`, `utils`,
`listeners`, `constants`, resources, `pom.xml`, suites, workflows) runs the smoke classes (LoginTest,
ResellerDashboardTest); docs-only changes run nothing. On iOS only classes with `ios`-group methods run. The
selected classes are listed in the run's summary. The check is advisory: keep it out of the branch protection
"required checks" so a red run never blocks the merge. For a full run use Actions > CI > Run workflow.

The full regression also runs by itself at 05:00 IST, Monday to Friday (cron `30 23 * * 0-4`, UTC), so its result
is ready before the day starts; the BrowserStack build is named "Daily Regression <run number>". The text below
describes that full run, which can also be started by hand.

### CI full run

The CI workflow's two platform jobs run one after the other (`max-parallel: 1`): first the
Android suite (`testng-parallel.xml`), then the iOS suite (`testng-ios.xml`). Each suite uses both of the BrowserStack
plan's 2 parallel sessions (2 workers, each taking the next waiting test class). Both platforms always run, so an
Android failure does not skip iOS. The iOS job needs the repository secret `IOS_BROWSERSTACK_APP_URL` (the
single-line `bs://` id of an IPA uploaded with `ios_app_groups_support=true`; uploads expire after about 30 days, so
re-upload when it does). A manual
run (Actions > CI > Run workflow) does only the platform picked in the form.

### Parallel run (what CI runs)

`mvn test -Dsuite.file=testng-parallel.xml` runs two suites one after the other: `testng-lanes.xml` (every class
except BridgesTest, 2 workers at once to match the BrowserStack plan; a free worker takes the next class), then
`testng-bridges.xml` (BridgesTest alone, because the account has a single attach ID). Check the note at the top of
`testng-lanes.xml` about classes sharing an account. `mvn test -Dsuite.file=testng-unit.xml` runs only the
framework unit tests (no device).

## Test Cases

Tests live in `src/test/java/tests/ts_<area>/`, one package per functional area.

| Test ID | Test Class | Description |
|---------|------------|-------------|
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

### Conventions

- **Waits**: the driver runs with implicit wait `0`. Every lookup goes through `utils.WaitUtils`;
  optional elements use the short timeouts in `constants.TimeoutConstants`.
- **Page objects**: pages do not inherit from a common parent; they import the shared parts they need.
  `Snackbar` (checks on the snackbar message) and `BottomNavigationBar` (click and check every bottom tab: Layouts,
  Tags, Search, More for an end user; Dashboard, Users, Business Portal, More for a reseller) are used by any page. The
  search bar, counter and filtered list that Dashboard > Cameras and a bridge's Cameras screen share are
  `CameraSearchFilter` (its public methods are the test-case steps); both pages hand it out through
  `cameraSearchFilter()`. Long-pressing a camera and closing its menu is `CameraContextMenu`. Locators used by several screens
  live in `locators.CommonLocators`.
- **Checks**: "this element must be showing" is `WaitUtils.assertVisible(driver, locator, message)` (and
  `assertNotVisible`), not a hand-written `isVisible` plus `Assert.assertTrue`.
- **Test setup**: `@BeforeMethod` uses `loginAndCreatePage(account, SomePage::new)` or
  `loginAndOpenDevicesDashboard(account)` from `BaseTest` instead of repeating login and page creation.
- **Cleanup**: tests that create data record it and remove it in `@AfterMethod`. Cleanup code never
  asserts and never throws (a throwing `@AfterMethod` skips the rest of the class in TestNG).
- **Test data**: `testData.json` is git-ignored; copy `testData.json.example` and fill it in. CI writes
  it from repository secrets. Key names in the workflow must match the example file.
- **Session reset**: `noReset=true` is the default, so `BaseTest.tearDown` logs out, falling back to
  `mobile: clearApp` when the test ended on a screen logout cannot navigate from.

## Reports

After test execution, reports are available at:

- **Extent Report**: `test-output/extent-reports/ExecutionReport_<timestamp>.html`
- **Surefire Reports**: `target/surefire-reports/index.html`
- **TestNG Results**: `target/surefire-reports/testng-results.xml`
- **Logs**: `test-output/logs/`

## Project Structure

```
MobileAutomation_EEN/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/           # BaseTest, DriverManager
│   │   │   ├── constants/      # Framework constants
│   │   │   ├── listeners/      # TestNG listeners
│   │   │   ├── pages/          # Page Object classes
│   │   │   ├── reports/        # Extent Reports manager
│   │   │   └── utils/          # Utility classes
│   │   └── resources/          # Configuration files
│   │       ├── config.properties
│   │       ├── android.properties
│   │       └── ios.properties
│   └── test/
│       ├── java/tests/         # Test classes
│       └── resources/          # Test data
│           └── testData.json
├── pom.xml                     # Maven configuration
├── testng.xml                  # TestNG suite (sequential)
├── testng-parallel.xml         # lanes suite, then BridgesTest alone (CI default)
├── testng-lanes.xml            # all classes except BridgesTest, 2 workers
├── testng-bridges.xml          # BridgesTest only
└── testng-unit.xml             # framework unit tests only
```

## Key Features

- ✅ Multi-platform support (Android & iOS)
- ✅ Local & Cloud (BrowserStack) execution
- ✅ Page Object Model design pattern
- ✅ Thread-safe driver management
- ✅ Comprehensive reporting (Extent Reports)
- ✅ Configurable waits and gestures
- ✅ JSON-based test data management
- ✅ Automatic permission handling
- ✅ Session management with proper cleanup

## Configuration Options

Override properties via command line:

```bash
mvn test -Dplatform=android -DappiumServerUrl=http://127.0.0.1:4724
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Run tests to ensure nothing breaks
4. Submit a pull request

## License

Internal use only - EEN Mobile Automation Team
