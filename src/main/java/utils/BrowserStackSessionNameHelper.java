package utils;
public final class BrowserStackSessionNameHelper {
    private BrowserStackSessionNameHelper() {
    }
    public static String resolveSessionName(String configuredName, String testClassName, String methodName) {
        return resolveSessionName(configuredName, testClassName, methodName, new Object[0]);
    }
    public static String resolveSessionName(String configuredName, String testClassName, String methodName,
                                            Object[] testParameters) {
        StringBuilder sessionName = new StringBuilder();
        if (configuredName != null && !configuredName.isBlank()) {
            sessionName.append(configuredName.trim());
        } else {
            if (testClassName != null && !testClassName.isBlank()) {
                sessionName.append(testClassName.trim());
            }
            if (methodName != null && !methodName.isBlank()) {
                if (!sessionName.isEmpty()) {
                    sessionName.append('.');
                }
                sessionName.append(methodName.trim());
            }
        }
        if (testParameters != null && testParameters.length > 0) {
            StringBuilder parameterValues = new StringBuilder();
            for (Object parameter : testParameters) {
                if (parameter == null) {
                    continue;
                }
                if (parameterValues.length() > 0) {
                    parameterValues.append("|");
                }
                parameterValues.append(parameter);
            }
            if (parameterValues.length() > 0) {
                if (!sessionName.isEmpty()) {
                    sessionName.append(" - ");
                }
                sessionName.append(parameterValues);
            }
        }
        return sessionName.length() > 0 ? sessionName.toString() : "MobileAutomationTest";
    }
}
