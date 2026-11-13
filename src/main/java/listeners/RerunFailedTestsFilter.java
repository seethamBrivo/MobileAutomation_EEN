package listeners;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

/**
 * Runs only the tests listed in the file given by {@code -DrerunTests=<file>}, one {@code package.Class#method} per
 * line, and leaves the suite itself as it is: the same classes, the same parallel workers, the same order, so
 * classes that must not run side by side still do not. Without the property every test runs.
 *
 * <p>A class none of whose tests are listed has no test method left, so TestNG does not run its
 * {@code @BeforeClass} and no device session is opened for it. All data-provider rows of a listed method run.
 *
 * <p>A listed test that depends on others ({@code dependsOnMethods}, {@code dependsOnGroups}) brings them along and
 * they run again too: TestNG refuses to start a run in which a kept method depends on one that was filtered out.
 * No suite used in CI has such a test today.
 *
 * <p>Limits. Methods are matched by class and name only. The listed tests run against whatever the earlier,
 * passing tests left on the shared accounts. This filter cannot tell that a listed test matches no method, because a
 * suite with several {@code <test>} blocks legitimately leaves most blocks empty: {@code scripts/test-summary.py}
 * makes that check from the results ({@code RERUN_LIST}) and fails the CI step, so a re-run that ran nothing is never
 * green.
 *
 * <p>Registered through {@code META-INF/services/org.testng.ITestNGListener}, so it applies to every suite file,
 * including the one generated for a pull request. The CI writes the file after each run
 * ({@code scripts/test-summary.py}) and passes it to the next attempt of a failed job.
 */
public class RerunFailedTestsFilter implements IMethodInterceptor {

    static final String PROPERTY = "rerunTests";

    private static final Map<String, Set<String>> LISTS = new ConcurrentHashMap<>();

    private static final Logger logger = LogManager.getLogger(RerunFailedTestsFilter.class);

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String file = System.getProperty(PROPERTY, "").trim();
        if (file.isEmpty()) {
            return methods;
        }
        // Read once per file, not once per <test> block of the suite (an iOS run has 15 of them).
        Set<String> listed = listedTestsOf(file);
        List<IMethodInstance> kept = keepListedWithTheirDependencies(methods, listed);
        logger.info("Re-running {} of {} test methods of '{}' from {}", kept.size(), methods.size(),
                context.getCurrentXmlTest().getName(), file);
        return kept;
    }

    /**
     * The listed methods and, transitively, what they depend on ({@code dependsOnMethods}, {@code dependsOnGroups}).
     * TestNG refuses to start a run in which a kept method depends on one that was filtered out ("is depending on
     * method ..., which is not annotated with @Test or not included"), so a dependency has to run again with it.
     */
    static List<IMethodInstance> keepListedWithTheirDependencies(List<IMethodInstance> methods, Set<String> listed) {
        Set<IMethodInstance> kept = new HashSet<>();
        Deque<IMethodInstance> pending = new ArrayDeque<>();
        for (IMethodInstance method : methods) {
            if (listed.contains(keyOf(method))) {
                kept.add(method);
                pending.add(method);
            }
        }
        while (!pending.isEmpty()) {
            ITestNGMethod dependent = pending.poll().getMethod();
            Set<String> methodsNeeded = Set.of(dependent.getMethodsDependedUpon());
            Set<String> groupsNeeded = Set.of(dependent.getGroupsDependedUpon());
            for (IMethodInstance candidate : methods) {
                ITestNGMethod method = candidate.getMethod();
                boolean needed = methodsNeeded.contains(method.getQualifiedName())
                        || Arrays.stream(method.getGroups()).anyMatch(groupsNeeded::contains);
                if (needed && kept.add(candidate)) {
                    logger.info("{} runs again too: {} depends on it", keyOf(candidate), dependent.getMethodName());
                    pending.add(candidate);
                }
            }
        }
        return methods.stream().filter(kept::contains).toList();
    }

    /** The list of a file, parsed once; a file that was rewritten (a different modification time) is read again. */
    static Set<String> listedTestsOf(String file) {
        long modified = Path.of(file).toFile().lastModified();
        return LISTS.computeIfAbsent(file + "|" + modified, key -> readListedTests(Path.of(file)));
    }

    static String keyOf(IMethodInstance method) {
        // The class that is run, as TestNG writes it to testng-results.xml (ITestClass.getRealClass()): for a test
        // inherited from a base class that is the subclass. Do not use ITestNGMethod.getRealClass(): that one is the
        // class that declares the method, so an inherited listed test would match no method and be filtered out.
        // RerunFailedTestsFilterTest.aTestInheritedByTwoClassesRunsOnlyInTheListedOne guards this.
        return method.getMethod().getTestClass().getRealClass().getName() + "#" + method.getMethod().getMethodName();
    }

    /** An unreadable or empty file throws: a wrong path must not turn a re-run into the whole suite, or into none. */
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
