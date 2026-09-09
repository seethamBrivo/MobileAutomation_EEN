package utils;

import constants.TestDataConstants;
import java.util.Random;

public final class TestDataGenerator {

    private static final String[] FIRST_NAMES = {
        "Alex", "Jordan", "Taylor", "Morgan", "Sam", "Chris", "Pat", "Riley", "Casey", "Jamie"
    };
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"
    };
    private static final Random RANDOM = new Random();

    private TestDataGenerator() {
    }

    public static String generateFirstName() {
        return FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
    }

    public static String generateLastName() {
        return LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
    }

    public static String generateEmail(String firstName, String lastName) {
        int randomNumber = TestDataConstants.MIN_RANDOM_SUFFIX + RANDOM.nextInt(TestDataConstants.MAX_RANDOM_RANGE);
        return String.format("%s.%s.%d@testdomain.com",
                firstName.toLowerCase().replaceAll("[^a-z0-9]", ""),
                lastName.toLowerCase().replaceAll("[^a-z0-9]", ""),
                randomNumber);
    }

    /** Unique, prefix-tagged name so pre-test cleanup can find leftovers by prefix. */
    public static String generateUniqueName(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
