package utils;

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
        // Private constructor to prevent instantiation of utility class
    }

    /**
     * Generates a random First Name.
     */
    public static String generateFirstName() {
        return FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
    }

    /**
     * Generates a random Last Name.
     */
    public static String generateLastName() {
        return LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
    }

    /**
     * Generates a unique Email based on the provided first and last name along with a random timestamp.
     * Example output: alex.smith.9482@testdomain.com
     */
    public static String generateEmail(String firstName, String lastName) {
        int randomNumber = 1000 + RANDOM.nextInt(9000);
        return String.format("%s.%s.%d@testdomain.com", 
                firstName.toLowerCase().replaceAll("[^a-z0-9]", ""), 
                lastName.toLowerCase().replaceAll("[^a-z0-9]", ""), 
                randomNumber);
    }

    /**
     * Generates a complete User Data container with pre-linked First Name, Last Name, and matching Email.
     */
    public static UserData generateRandomUser() {
        String firstName = generateFirstName();
        String lastName = generateLastName();
        String email = generateEmail(firstName, lastName);
        return new UserData(firstName, lastName, email);
    }

    /**
     * Simple Data Transfer Object to hold generated user details.
     */
    public static class UserData {
        private final String firstName;
        private final String lastName;
        private final String email;

        public UserData(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}