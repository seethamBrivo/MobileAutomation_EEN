package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import constants.TestDataConstants;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public final class JsonDataReader {

    private static volatile JsonObject testData;

    private JsonDataReader() {
    }

    public static JsonObject readJsonObject(String resourcePath) {
        String json = loadResourceAsString(resourcePath);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    /** The parsed {@code testData.json}, read once per JVM. */
    public static JsonObject getTestData() {
        JsonObject data = testData;
        if (data == null) {
            synchronized (JsonDataReader.class) {
                data = testData;
                if (data == null) {
                    data = readJsonObject(TestDataConstants.TEST_DATA_FILE);
                    testData = data;
                }
            }
        }
        return data;
    }

    /** The {@code credentials.<accountType>} block, e.g. {@code reseller_user_creds}. */
    public static JsonObject getCredentials(String accountType) {
        JsonObject credentials = getTestData().getAsJsonObject("credentials");
        if (credentials == null || !credentials.has(accountType)) {
            throw new IllegalStateException(
                    "testData.json has no credentials block '" + accountType + "'");
        }
        return credentials.getAsJsonObject(accountType);
    }

    /**
     * A string value from {@code credentials.<accountType>.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing.
     */
    public static String getCredentialValue(String accountType, String key) {
        JsonObject credentials = getCredentials(accountType);
        return getNestedBlockValue(credentials, "credentials." + accountType, key);
    }

    /**
     * Helper for retrieving a value from a nested block with consistent validation.
     */
    private static String getNestedBlockValue(JsonObject block, String blockPath, String key) {
        JsonElement value = block.get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalStateException(
                    "testData.json is missing key '" + key + "' under " + blockPath
                    + " (check the CI workflow and testData.json.example use the same key names)");
        }
        return value.getAsString();
    }

    /**
     * Generic helper to retrieve a value from any test data block with consistent validation.
     * Eliminates duplication across the credentials and the account-data blocks.
     */
    private static JsonElement getBlockValue(String blockConstant, String blockName, String key) {
        JsonObject block = getTestData().getAsJsonObject(blockConstant);
        if (block == null) {
            throw new IllegalStateException(
                    "testData.json is missing '" + blockConstant + "' block "
                    + "(check the CI workflow and testData.json.example have " + blockName + " section)");
        }
        JsonElement value = block.get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalStateException(
                    "testData.json is missing key '" + key + "' under " + blockName);
        }
        return value;
    }

    /**
     * A string value from {@code mechanical_zoom_camera_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing or null.
     */
    public static String getMechanicalZoomCameraValue(String key) {
        return getBlockValue(
                TestDataConstants.MECHANICAL_ZOOM_CAMERA_DATA,
                "mechanical_zoom_camera_data",
                key
        ).getAsString();
    }

    /**
     * A string value from {@code switchable_camera_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing or null.
     */
    public static String getSwitchableCameraValue(String key) {
        return getBlockValue(
                TestDataConstants.SWITCHABLE_CAMERA_DATA,
                TestDataConstants.SWITCHABLE_CAMERA_DATA,
                key
        ).getAsString();
    }

    /**
     * A string value from {@code archive_search_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing or null.
     */
    public static String getArchiveSearchValue(String key) {
        return getBlockValue(
                TestDataConstants.ARCHIVE_SEARCH_DATA,
                TestDataConstants.ARCHIVE_SEARCH_DATA,
                key
        ).getAsString();
    }

    private static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to read resource: " + resourcePath, exception);
        }
    }
}
