package listeners;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

/**
 * Guards {@link RetryTransformer} against TestNG upgrades. The "no analyzer declared" value is read
 * from {@code @Test}'s own default rather than hard-coded, so if TestNG renames or moves its
 * internal placeholder this test still sees the real value and fails when the transformer stops
 * attaching {@link RetryAnalyzer} (which would otherwise silently disable all retries).
 */
public class RetryTransformerTest {

    /** What TestNG puts on a {@code @Test} that declares no retryAnalyzer. */
    private static Class<?> testNgDefaultAnalyzer() throws Exception {
        return (Class<?>) Test.class.getMethod("retryAnalyzer").getDefaultValue();
    }

    /** Minimal ITestAnnotation: answers getRetryAnalyzerClass and records setRetryAnalyzer. */
    private static ITestAnnotation annotationDeclaring(Class<?> declared, AtomicReference<Class<?>> assigned) {
        InvocationHandler handler = (proxy, method, args) -> {
            switch (method.getName()) {
                case "getRetryAnalyzerClass":
                    return declared;
                case "setRetryAnalyzer":
                    assigned.set((Class<?>) args[0]);
                    return null;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        };
        return (ITestAnnotation) Proxy.newProxyInstance(
                ITestAnnotation.class.getClassLoader(), new Class<?>[] {ITestAnnotation.class}, handler);
    }

    private static Class<?> analyzerAfterTransform(Class<?> declared) {
        AtomicReference<Class<?>> assigned = new AtomicReference<>();
        new RetryTransformer().transform(annotationDeclaring(declared, assigned), null, null, null);
        return assigned.get();
    }

    @Test(description = "an @Test with TestNG's own default (no analyzer declared) gets RetryAnalyzer")
    public void testNgDefaultGetsRetryAnalyzer() throws Exception {
        assertEquals(analyzerAfterTransform(testNgDefaultAnalyzer()), RetryAnalyzer.class);
    }

    @Test(description = "a null analyzer also gets RetryAnalyzer")
    public void nullGetsRetryAnalyzer() {
        assertEquals(analyzerAfterTransform(null), RetryAnalyzer.class);
    }

    @Test(description = "an analyzer declared by the test is left alone")
    public void ownAnalyzerIsKept() {
        assertEquals(analyzerAfterTransform(NeverRetry.class), null, "must not overwrite the test's own analyzer");
    }

    @Test(description = "only TestNG-internal placeholders count as 'no analyzer declared'")
    public void ownAnalyzerDetection() throws Exception {
        assertFalse(RetryTransformer.hasOwnRetryAnalyzer(null));
        assertFalse(RetryTransformer.hasOwnRetryAnalyzer(testNgDefaultAnalyzer()));
        assertTrue(RetryTransformer.hasOwnRetryAnalyzer(NeverRetry.class));
    }

    public static class NeverRetry implements IRetryAnalyzer {
        @Override
        public boolean retry(ITestResult result) {
            return false;
        }
    }
}
