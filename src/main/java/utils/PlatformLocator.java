package utils;

import org.openqa.selenium.By;

/**
 * Picks the locator that matches the platform under test, so one locator class holds both platforms' values and
 * the pages stay shared. The platform comes from {@code -Dplatform} (or config.properties) and does not change
 * during a run, so the choice is made once, when the locator constant is initialised.
 */
public final class PlatformLocator {

    private PlatformLocator() {}

    public static boolean isIos() {
        return "ios".equalsIgnoreCase(ConfigReader.getPlatform());
    }

    public static By get(By android, By ios) {
        return isIos() ? ios : android;
    }
}
