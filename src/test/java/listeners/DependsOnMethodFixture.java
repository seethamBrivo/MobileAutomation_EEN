package listeners;

import org.testng.annotations.Test;

/**
 * A test class for RerunFailedTestsFilterTest only (it is in no suite). It is top-level because TestNG cannot resolve
 * {@code dependsOnMethods} inside a nested class.
 */
public class DependsOnMethodFixture {

    @Test
    public void first() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnMethod.first");
    }

    @Test(dependsOnMethods = "first")
    public void second() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnMethod.second");
    }

    @Test
    public void unrelated() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnMethod.unrelated");
    }
}
