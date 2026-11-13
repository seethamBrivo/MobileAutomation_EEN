package listeners;

import org.testng.annotations.Test;

/** A test class for RerunFailedTestsFilterTest only (it is in no suite); top-level for the same reason as its sibling. */
public class DependsOnGroupFixture {

    @Test(groups = "prepared")
    public void prepare() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnGroup.prepare");
    }

    @Test(dependsOnGroups = "prepared")
    public void verify() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnGroup.verify");
    }

    @Test
    public void unrelated() {
        RerunFailedTestsFilterTest.RAN.add("DependsOnGroup.unrelated");
    }
}
