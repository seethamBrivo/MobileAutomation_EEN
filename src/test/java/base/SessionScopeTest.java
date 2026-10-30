package base;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SessionScopeTest {

    @DataProvider(name = "cases")
    public Object[][] cases() {
        return new Object[][]{
                {null, "android", SessionScope.CLASS},
                {"", "android", SessionScope.CLASS},
                {"class", "android", SessionScope.CLASS},
                {"CLASS", "android", SessionScope.CLASS},
                {"method", "android", SessionScope.METHOD},
                {" Method ", "android", SessionScope.METHOD},
                {"anything-else", "android", SessionScope.CLASS},
                // iOS follows the same rule as Android (its login page implements relaunch / landing-screen checks).
                {null, "ios", SessionScope.CLASS},
                {"class", "ios", SessionScope.CLASS},
                {"method", "ios", SessionScope.METHOD},
                // Any other platform has no relaunch / landing-screen checks.
                {null, "windows", SessionScope.METHOD},
                {"class", null, SessionScope.METHOD},
        };
    }

    @Test(dataProvider = "cases")
    public void resolvesScope(String configured, String platform, SessionScope expected) {
        assertEquals(SessionScope.resolve(configured, platform), expected);
    }
}
