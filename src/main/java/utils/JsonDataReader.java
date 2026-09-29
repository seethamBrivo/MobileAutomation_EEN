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
        JsonElement value = getCredentials(accountType).get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalStateException(
                    "testData.json is missing key '" + key + "' under credentials." + accountType
                    + " (check the CI workflow and testData.json.example use the same key names)");
        }
        return value.getAsString();
    }

    /**
     * A string value from {@code search_filter.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing.
     */
    public static String getSearchFilterValue(String key) {
        return getBlockValue(TestDataConstants.SEARCH_FILTER, "search_filter", key).getAsString();
    }

    /**
     * A string value from {@code layout_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing.
     */
    public static String getLayoutDataValue(String key) {
        JsonElement value = getLayoutDataElement(key);
        return value.getAsString();
    }

    /**
     * An integer value from {@code layout_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing.
     */
    public static int getLayoutDataIntValue(String key) {
        JsonElement value = getLayoutDataElement(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new IllegalStateException(
                    "Expected integer for layout_data.'" + key + "' but got: " + value);
        }
        return value.getAsInt();
    }

    /**
     * A boolean value from {@code layout_data.<key>}. Fails with the key name
     * instead of a bare NullPointerException when the key is missing.
     */
    public static boolean getLayoutDataBooleanValue(String key) {
        JsonElement value = getLayoutDataElement(key);
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalStateException(
                    "Expected boolean for layout_data.'" + key + "' but got: " + value);
        }
        return value.getAsBoolean();
    }

    /**
     * Generic helper to retrieve a value from any test data block with consistent validation.
     * Eliminates duplication across credentials, search_filter, layout_data, etc.
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
     * Helper to retrieve a layout_data element (uses the generic getBlockValue helper).
     */
    private static JsonElement getLayoutDataElement(String key) {
        return getBlockValue(TestDataConstants.LAYOUT_DATA, "layout_data", key);
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
