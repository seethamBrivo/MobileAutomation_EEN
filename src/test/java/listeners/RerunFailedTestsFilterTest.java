package listeners;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/**
 * Runs two small test classes through a real TestNG, so it also proves what the CI relies on: the filter is picked
 * up from META-INF/services without being listed in any suite file, only the listed methods run, and a class with
 * no listed method does not even run its {@code @BeforeClass} (which is where a real test opens its device session).
 *
 * <p>Not parallel-safe: it sets the JVM-wide {@code rerunTests} property for the length of a nested TestNG run, and a
 * suite that started a test meanwhile would be filtered by it. Keep it in the serial unit suite, never in a lane that
 * runs device tests beside it (like RetryAnalyzerTest, it is left out of testng-lanes.xml for the same reason).
 */
public class RerunFailedTestsFilterTest {

    /** What ran, in order, as "Class.method" or "Class.beforeClass". */
    static final List<String> RAN = Collections.synchronizedList(new ArrayList<>());

    public static class FirstClass {
        @BeforeClass
        public void beforeClass() {
            RAN.add("FirstClass.beforeClass");
        }

        @Test
        public void one() {
            RAN.add("FirstClass.one");
        }

        @Test
        public void two() {
            RAN.add("FirstClass.two");
        }
    }

    public static class SecondClass {
        @BeforeClass
        public void beforeClass() {
            RAN.add("SecondClass.beforeClass");
        }

        @Test
        public void three() {
            RAN.add("SecondClass.three");
        }
    }

    /** A test that two subclasses inherit: the filter must tell the two copies apart. */
    public static class BaseWithTest {
        @Test
        public void inherited() {
            RAN.add(getClass().getSimpleName() + ".inherited");
        }
    }

    public static class ChildOne extends BaseWithTest {}

    public static class ChildTwo extends BaseWithTest {}

    private static List<String> runWith(String rerunFile) {
        return runWith(rerunFile, FirstClass.class, SecondClass.class);
    }

    private static List<String> runWith(String rerunFile, Class<?>... classes) {
        return run(rerunFile, testng -> testng.setTestClasses(classes));
    }

    private static List<String> run(String rerunFile, java.util.function.Consumer<TestNG> setUp) {
        RAN.clear();
        String before = System.getProperty(RerunFailedTestsFilter.PROPERTY);
        try {
            if (rerunFile == null) {
                System.clearProperty(RerunFailedTestsFilter.PROPERTY);
            } else {
                System.setProperty(RerunFailedTestsFilter.PROPERTY, rerunFile);
            }
            TestNG testng = new TestNG(false);
            testng.setUseDefaultListeners(false);
            setUp.accept(testng);
            testng.run();
        } finally {
            if (before == null) {
                System.clearProperty(RerunFailedTestsFilter.PROPERTY);
            } else {
                System.setProperty(RerunFailedTestsFilter.PROPERTY, before);
            }
        }
        return new ArrayList<>(RAN);
    }

    private static String listOf(String... tests) throws IOException {
        Path file = Files.createTempFile("rerun-tests", ".txt");
        file.toFile().deleteOnExit();
        Files.write(file, List.of(tests));
        return file.toString();
    }

    private static String name(Class<?> cls) {
        return cls.getName();
    }

    @Test
    public void withoutTheListEveryTestRuns() {
        List<String> ran = runWith(null);

        assertEquals(Set.copyOf(ran), Set.of("FirstClass.beforeClass", "FirstClass.one", "FirstClass.two",
                "SecondClass.beforeClass", "SecondClass.three"), "Every test and class setup should run: " + ran);
    }

    @Test
    public void onlyTheListedMethodRunsAndAClassWithNoListedMethodIsLeftAlone() throws IOException {
        List<String> ran = runWith(listOf(name(FirstClass.class) + "#two"));

        assertEquals(ran, List.of("FirstClass.beforeClass", "FirstClass.two"),
                "Only FirstClass.two should run, and SecondClass should not even be set up");
    }

    @Test
    public void methodsOfSeveralClassesCanBeListed() throws IOException {
        List<String> ran = runWith(listOf(name(FirstClass.class) + "#one", name(SecondClass.class) + "#three"));

        assertEquals(Set.copyOf(ran), Set.of("FirstClass.beforeClass", "FirstClass.one", "SecondClass.beforeClass",
                "SecondClass.three"), "The listed method of each class should run: " + ran);
    }

    @Test
    public void aTestInheritedByTwoClassesRunsOnlyInTheListedOne() throws IOException {
        List<String> ran = runWith(listOf(name(ChildOne.class) + "#inherited"), ChildOne.class, ChildTwo.class);

        assertEquals(ran, List.of("ChildOne.inherited"), "Only the listed subclass should run its inherited test");
    }

    @Test
    public void theClassNameTheFilterUsesIsTheOneTestNgWritesToTheResultsFile() throws Exception {
        Path reports = Files.createTempDirectory("rerun-reports");
        run(null, testng -> {
            testng.setTestClasses(new Class[] {ChildOne.class});
            testng.setUseDefaultListeners(true);
            testng.setOutputDirectory(reports.toString());
        });

        String results = Files.readString(reports.resolve("testng-results.xml"));
        assertTrue(results.contains("<class name=\"" + name(ChildOne.class) + "\""),
                "scripts/test-summary.py lists tests by the class name in the results, which should be "
                        + name(ChildOne.class) + " for an inherited test, but the file was: " + results);
    }

    @Test
    public void aSuiteWithSeveralTestBlocksRunsTheListedTestAndLeavesTheEmptyBlockAlone() throws IOException {
        List<String> ran = run(listOf(name(FirstClass.class) + "#one"), testng -> {
            XmlSuite suite = new XmlSuite();
            suite.setName("two blocks");
            for (Class<?> cls : List.of(FirstClass.class, SecondClass.class)) {
                XmlTest block = new XmlTest(suite);
                block.setName("block of " + cls.getSimpleName());
                block.setXmlClasses(List.of(new XmlClass(cls)));
            }
            testng.setXmlSuites(List.of(suite));
        });

        assertEquals(ran, List.of("FirstClass.beforeClass", "FirstClass.one"),
                "The block with the listed test should run it, and the block with none should run nothing");
    }

    @Test
    public void blankLinesAndCommentsInTheListAreIgnored() throws IOException {
        Path file = Files.createTempFile("rerun-tests", ".txt");
        file.toFile().deleteOnExit();
        Files.write(file, List.of("// the failed tests of attempt 1", "", "  a.B#c  ", "   "));

        assertEquals(RerunFailedTestsFilter.readListedTests(file), Set.of("a.B#c"),
                "Only the test line should be read, trimmed");
    }

    @Test
    public void aMissingListFailsInsteadOfRunningEverything() {
        assertThrows(UncheckedIOException.class,
                () -> RerunFailedTestsFilter.readListedTests(Path.of("no-such-dir", "no-such-file.txt")));
    }

    @Test
    public void anEmptyListFailsInsteadOfRunningNothing() throws IOException {
        Path file = Files.createTempFile("rerun-tests", ".txt");
        file.toFile().deleteOnExit();
        Files.write(file, List.of("// nothing failed", ""));

        assertThrows(IllegalStateException.class, () -> RerunFailedTestsFilter.readListedTests(file));
    }
}
