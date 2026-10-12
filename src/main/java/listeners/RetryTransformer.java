package listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/** Attaches {@link RetryAnalyzer} to every {@code @Test} that does not declare its own. */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null
                || annotation.getRetryAnalyzerClass().getName().equals("org.testng.internal.annotations.DisabledRetryAnalyzer")) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
