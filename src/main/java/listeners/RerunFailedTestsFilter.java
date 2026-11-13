package listeners;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/**
 * Runs only the tests listed in the file given by {@code -DrerunTests=<file>}, one {@code package.Class#method} per
 * line, and leaves the suite itself as it is: the same classes, the same parallel workers, the same order, so
 * classes that must not run side by side still do not. Without the property every test runs.
 *
 * <p>A class none of whose tests are listed has no test method left, so TestNG does not run its
 * {@code @BeforeClass} and no device session is opened for it. All data-provider rows of a listed method run.
 *
 * <p>Registered through {@code META-INF/services/org.testng.ITestNGListener}, so it applies to every suite file,
 * including the one generated for a pull request. The CI writes the file after each run
 * ({@code scripts/test-summary.py}) and passes it to the next attempt of a failed job.
 */
public class RerunFailedTestsFilter implements IMethodInterceptor {

    static final String PROPERTY = "rerunTests";

    private static final Logger logger = LogManager.getLogger(RerunFailedTestsFilter.class);

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String file = System.getProperty(PROPERTY, "").trim();
        if (file.isEmpty()) {
            return methods;
        }
        Set<String> listed = readListedTests(Path.of(file));
        List<IMethodInstance> kept = methods.stream().filter(m -> listed.contains(keyOf(m))).toList();
        logger.info("Re-running {} of {} test methods of '{}' from {}", kept.size(), methods.size(),
                context.getCurrentXmlTest().getName(), file);
        return kept;
    }

    static String keyOf(IMethodInstance method) {
        return method.getMethod().getRealClass().getName() + "#" + method.getMethod().getMethodName();
    }

    /** Fails loudly: a wrong path must not turn a "re-run the failed tests" into the whole suite, or into none. */
    static Set<String> readListedTests(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException("-D" + PROPERTY + " names a file that cannot be read: " + file, e);
        }
        Set<String> listed = new HashSet<>();
        for (String line : lines) {
            String test = line.trim();
            if (!test.isEmpty() && !test.startsWith("//")) {
                listed.add(test);
            }
        }
        if (listed.isEmpty()) {
            throw new IllegalStateException("-D" + PROPERTY + " names a file that lists no tests: " + file);
        }
        return listed;
    }
}
