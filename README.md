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
`listeners.RetryTransformer` in `testng.xml`). Each attempt gets a fresh Appium session, and a
failed attempt keeps its screenshot in the report and is marked failed in BrowserStack.

```bash
mvn test -Dplatform=android -DretryCount=2   # up to 2 extra attempts
mvn test -Dplatform=android -DretryCount=0   # disable retries
```

TestNG does not retry failed `@BeforeMethod` methods; login has its own retry in `LoginPage.loginAs`.
A test that only passes on retry is flaky: check the earlier attempt's session before ignoring it.

## Test Cases

Tests live in `src/test/java/tests/ts_<area>/`, one package per functional area.

| Test ID | Test Class | Description |
|---------|------------|-------------|
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
| EEPD-TC-37646 | ts_vsp_events.VSPEventsTest | VSP Events - Card display, pagination, and orientation testing |
| EEPD-TC-37714 | ts_vsp_events.VSPEventsTest | VSP Events - More info detail page verification with 17 fields |
| EEPD-TC-3920 | ts_my_profile.MyProfileTest | POS-My Profile - setting 'Time zone' in VMS mobile app |

### Conventions

- **Waits**: the driver runs with implicit wait `0`. Every lookup goes through `utils.WaitUtils`;
  optional elements use the short timeouts in `constants.TimeoutConstants`.
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
└── testng.xml                  # TestNG suite
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
