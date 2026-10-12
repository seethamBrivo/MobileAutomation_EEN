package listeners;

import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import utils.ConfigReader;

/**
 * Switches the suite to parallel execution from the command line, so testng.xml stays sequential
 * by default.
 *
 * <ul>
 *   <li>{@code -DthreadCount=N} - number of parallel sessions; 1 (default) keeps the suite sequential.
 *   <li>{@code -DparallelMode=tests|classes} - what runs in parallel; default {@code tests}, i.e. each
 *       {@code <test>} block of testng.xml gets its own thread while the classes inside one block
 *       stay sequential. That keeps the three bridge classes (one shared attach ID) in order.
 * </ul>
 *
 * Parallel runs need one device per thread, so they are only honoured for BrowserStack. A local run
 * has a single device and falls back to sequential. Keep N within the BrowserStack plan's parallel
 * limit, otherwise sessions queue or are rejected.
 */
public class ParallelConfigListener implements IAlterSuiteListener {

    private static final Logger logger = LogManager.getLogger(ParallelConfigListener.class);

    @Override
    public void alter(List<XmlSuite> suites) {
        int threads = parseThreadCount(read("threadCount"));
        if (threads <= 1) {
            return;
        }
        if (!"browserstack".equalsIgnoreCase(read("executionProvider"))) {
            logger.warn("threadCount={} ignored: parallel execution needs one device per thread, "
                    + "which only the BrowserStack provider offers. Running sequentially.", threads);
            return;
        }
        XmlSuite.ParallelMode mode = parseMode(read("parallelMode"));
        for (XmlSuite suite : suites) {
            suite.setParallel(mode);
            suite.setThreadCount(threads);
        }
        logger.info("Parallel execution enabled: mode={}, threads={}", mode, threads);
    }

    static int parseThreadCount(String value) {
        try {
            return value.isBlank() ? 1 : Math.max(1, Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            logger.warn("Invalid threadCount '{}', running sequentially", value);
            return 1;
        }
    }

    static XmlSuite.ParallelMode parseMode(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "classes" -> XmlSuite.ParallelMode.CLASSES;
            case "", "tests" -> XmlSuite.ParallelMode.TESTS;
            default -> {
                logger.warn("Unsupported parallelMode '{}' (use tests or classes), using tests", value);
                yield XmlSuite.ParallelMode.TESTS;
            }
        };
    }

    /** System property first, then config.properties; a missing config file counts as unset. */
    private static String read(String key) {
        String fromSystem = System.getProperty(key, "").trim();
        if (!fromSystem.isBlank()) {
            return fromSystem;
        }
        try {
            return ConfigReader.getProperty(key);
        } catch (RuntimeException e) {
            return "";
        }
    }
}
