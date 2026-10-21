package utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.testng.annotations.Test;

public class BrowserStackStatusTest {

    private static final String PREFIX = "browserstack_executor: ";

    @Test
    public void statusScriptIsValidJson() throws Exception {
        String script = BrowserStackStatus.statusScript(true, "All tests passed");
        assertTrue(script.startsWith(PREFIX));
        JsonObject json = JsonParser.parseString(script.substring(PREFIX.length())).getAsJsonObject();
        assertEquals(json.get("action").getAsString(), "setSessionStatus");
        assertEquals(json.getAsJsonObject("arguments").get("status").getAsString(), "passed");
        assertEquals(json.getAsJsonObject("arguments").get("reason").getAsString(), "All tests passed");
    }

    @Test
    public void failedStatusAndHostileReasonStillProduceValidJson() throws Exception {
        String script = BrowserStackStatus.statusScript(false, "expected \"a\" but\nwas C:\\temp\r\nnext");
        JsonObject json = JsonParser.parseString(script.substring(PREFIX.length())).getAsJsonObject();
        assertEquals(json.getAsJsonObject("arguments").get("status").getAsString(), "failed");
        assertFalse(json.getAsJsonObject("arguments").get("reason").getAsString().contains("\n"));
    }

    @Test
    public void tabsOtherControlCharactersAndJsonSyntaxStayInsideTheString() throws Exception {
        String script = BrowserStackStatus.annotateScript("a\tb\u0001c } { : , \"} x", "info");
        JsonObject json = JsonParser.parseString(script.substring(PREFIX.length())).getAsJsonObject();
        assertEquals(json.getAsJsonObject("arguments").get("data").getAsString(), "a b c } { : ,  } x");
    }

    @Test
    public void longTextIsTruncatedAndNullIsEmpty() {
        assertEquals(BrowserStackStatus.sanitize("x".repeat(1000)).length(), 250);
        assertEquals(BrowserStackStatus.sanitize(null), "");
    }

    @Test
    public void annotateScriptIsValidJson() throws Exception {
        String script = BrowserStackStatus.annotateScript("Start BridgesTest.test01");
        JsonObject json = JsonParser.parseString(script.substring(PREFIX.length())).getAsJsonObject();
        assertEquals(json.get("action").getAsString(), "annotate");
        assertEquals(json.getAsJsonObject("arguments").get("data").getAsString(), "Start BridgesTest.test01");
    }

    @Test
    public void sessionReasonSummarisesPassedFailedAndNotRun() {
        assertEquals(BrowserStackStatus.sessionReason(3, List.of(), List.of()), "3 passed, 0 failed of 3");
        assertEquals(BrowserStackStatus.sessionReason(6, List.of("test03_list"), List.of()),
                "5 passed, 1 failed of 6 | FAILED: test03_list");
        assertEquals(BrowserStackStatus.sessionReason(4, List.of("a"), List.of("b", "c")),
                "1 passed, 1 failed, 2 not run of 4 | FAILED: a | NOT RUN: b, c");
    }

    @Test
    public void annotateScriptCarriesTheLevel() throws Exception {
        String script = BrowserStackStatus.annotateScript("FAILED test03", "error");
        JsonObject json = JsonParser.parseString(script.substring(PREFIX.length())).getAsJsonObject();
        assertEquals(json.getAsJsonObject("arguments").get("level").getAsString(), "error");
    }
}
