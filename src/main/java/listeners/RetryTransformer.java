package listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/** Attaches {@link RetryAnalyzer} to every {@code @Test} that does not declare its own. */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (!hasOwnRetryAnalyzer(annotation.getRetryAnalyzerClass())) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }

    /**
     * An unset analyzer is either null or TestNG's internal "disabled" placeholder. Rather than
     * matching that placeholder's exact name (a TestNG upgrade could rename it and silently turn
     * retries off), treat any analyzer that ships inside TestNG as "not declared by the test".
     * RetryTransformerTest guards this against upgrades.
     */
    static boolean hasOwnRetryAnalyzer(Class<?> analyzer) {
        return analyzer != null && !analyzer.getName().startsWith("org.testng.");
    }
}
