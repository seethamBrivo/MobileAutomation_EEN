package tests.ios_ts_diagnostics;

import base.BaseTest;
import constants.TestDataConstants;
import io.appium.java_client.AppiumBy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * iOS discovery only: signs in, then replays the steps of a script file and saves the hierarchy and a
 * screenshot wherever the script says so. A failing step is logged and skipped, so one session can walk
 * many screens.
 *
 * <pre>
 * mvn test -Dplatform=ios -DexecutionProvider=browserstack -Dsuite.file=testng-ios-explore.xml \
 *     -Dexplore.script=path/to/steps.txt [-Dexplore.creds=ts02|reseller]
 * </pre>
 *
 * Script lines (blank lines and lines starting with # are ignored); a locator is {@code strategy=value}
 * where the strategy is {@code id} (accessibility id), {@code pred} (NSPredicate) or {@code xpath}:
 * <pre>
 * tap pred=type == 'XCUIElementTypeButton' AND name == 'More'
 * type id=search_field :: some text
 * dump after-more
 * wait 3
 * scroll down
 * back
 * alert accept
 * hold id=some_row
 * tapxy 20 780
 * </pre>
 */
public class IosExploreTest extends BaseTest {

    private static final Path OUT = Path.of("target", "page-sources");

    @Test(description = "iOS discovery: sign in and replay the explore script")
    public void explore() throws IOException {
        String script = System.getProperty("explore.script");
        if (script == null) {
            throw new IllegalArgumentException("Pass -Dexplore.script=<steps file>");
        }
        String creds = switch (String.valueOf(System.getProperty("explore.creds")).toLowerCase()) {
            case "ts02" -> TestDataConstants.TS02_CREDS;
            case "reseller" -> TestDataConstants.RESELLER_CREDS;
            default -> TestDataConstants.ENDUSER_CREDS;
        };
        iosLoginAs(creds);
        logger.info("EXPLORE session id {}", driver.getSessionId());
        dump("00-after-login");

        List<String> lines = Files.readAllLines(Path.of(script), StandardCharsets.UTF_8);
        int n = 0;
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            n++;
            logger.info("EXPLORE step {}: {}", n, line);
            try {
                run(line);
            } catch (RuntimeException | InterruptedException e) {
                logger.warn("EXPLORE step {} failed: {}", n, firstLine(e.toString()));
                dump(String.format("%02d-FAILED", n));
            }
        }
    }

    private void run(String line) throws InterruptedException {
        String[] head = line.split("\\s+", 2);
        String cmd = head[0];
        String arg = head.length > 1 ? head[1] : "";
        switch (cmd) {
            case "tap" -> driver.findElement(parse(arg)).click();
            case "type" -> {
                String[] parts = arg.split(" :: ", 2);
                WebElement el = driver.findElement(parse(parts[0]));
                el.click();
                el.sendKeys(parts[1]);
            }
            case "hold" -> {
                org.openqa.selenium.Rectangle r = driver.findElement(parse(arg)).getRect();
                driver.executeScript("mobile: touchAndHold", Map.of(
                        "x", (double) (r.x + r.width / 2), "y", (double) (r.y + r.height / 2), "duration", 2.0));
            }
            case "tapxy" -> {
                String[] xy = arg.split("\\s+");
                driver.executeScript("mobile: tap", Map.of("x", Double.parseDouble(xy[0]), "y", Double.parseDouble(xy[1])));
            }
            case "dump" -> dump(arg);
            case "wait" -> Thread.sleep(Long.parseLong(arg) * 1000);
            case "scroll" -> driver.executeScript("mobile: scroll", Map.of("direction", arg));
            case "swipe" -> driver.executeScript("mobile: swipe", Map.of("direction", arg));
            case "back" -> driver.navigate().back();
            case "alert" -> driver.executeScript("mobile: alert", Map.of("action", arg));
            case "text" -> logger.info("EXPLORE text [{}] = '{}'", arg, driver.findElement(parse(arg)).getText());
            case "attr" -> {
                String[] parts = arg.split(" :: ", 2);
                logger.info("EXPLORE attr [{}] {} = '{}'", parts[0], parts[1],
                        driver.findElement(parse(parts[0])).getAttribute(parts[1]));
            }
            case "count" -> logger.info("EXPLORE count [{}] = {}", arg, driver.findElements(parse(arg)).size());
            default -> throw new IllegalArgumentException("Unknown step: " + cmd);
        }
    }

    private static By parse(String locator) {
        int eq = locator.indexOf('=');
        String strategy = locator.substring(0, eq).strip();
        String value = locator.substring(eq + 1).strip();
        return switch (strategy) {
            case "id" -> AppiumBy.accessibilityId(value);
            case "pred" -> AppiumBy.iOSNsPredicateString(value);
            case "xpath" -> AppiumBy.xpath(value);
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }

    private void dump(String name) {
        try {
            Files.createDirectories(OUT);
            Files.writeString(OUT.resolve("ex-" + name + ".xml"), driver.getPageSource(), StandardCharsets.UTF_8);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(OUT.resolve("ex-" + name + ".png"), png);
            logger.info("EXPLORE saved '{}'", name);
        } catch (IOException | RuntimeException e) {
            logger.warn("EXPLORE could not save '{}': {}", name, firstLine(e.toString()));
        }
    }

    private static String firstLine(String s) {
        int nl = s.indexOf('\n');
        return nl < 0 ? s : s.substring(0, nl);
    }
}
