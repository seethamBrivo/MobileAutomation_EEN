package base;

import java.util.Locale;
import utils.ConfigReader;

/**
 * How long one Appium session (and the login inside it) lives.
 *
 * <ul>
 *   <li>{@link #CLASS} (default): one session per test class. The first test logs in; later tests of the
 *       class reuse the login and only relaunch the app to get back to the landing screen.</li>
 *   <li>{@link #METHOD}: the original behaviour, a fresh session plus a UI login and logout per test.
 *       Select with {@code -DsessionScope=method}; also used on platforms other than Android and iOS, whose
 *       page objects do not implement the relaunch/landing-screen checks.</li>
 * </ul>
 */
public enum SessionScope {
    CLASS,
    METHOD;

    public static SessionScope current() {
        return resolve(ConfigReader.getProperty("sessionScope"), ConfigReader.getPlatform());
    }

    static SessionScope resolve(String configured, String platform) {
        String normalized = platform == null ? "" : platform.trim();
        if (!"android".equalsIgnoreCase(normalized) && !"ios".equalsIgnoreCase(normalized)) {
            return METHOD;
        }
        if (configured == null || configured.isBlank()) {
            return CLASS;
        }
        return "method".equals(configured.trim().toLowerCase(Locale.ROOT)) ? METHOD : CLASS;
    }
}
