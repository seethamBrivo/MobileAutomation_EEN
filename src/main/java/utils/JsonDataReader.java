package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public final class JsonDataReader {

    private JsonDataReader() {
    }

    public static JsonObject readJsonObject(String resourcePath) {
        String json = loadResourceAsString(resourcePath);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static JsonObject getCredentials(String accountType) {
        JsonObject testData = readJsonObject("testData.json");

        return testData
                .getAsJsonObject("credentials")
                .getAsJsonObject(accountType);
    }

    private static String loadResourceAsString(String resourcePath) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Resource not found: " + resourcePath
                );
            }

            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Unable to read resource: " + resourcePath,
                    exception
            );
        }
    }
}