package base;

/**
 * Which account the app is currently signed in as, for the Appium session running on this thread.
 * Per thread because parallel lanes each own a device session. Cleared whenever the session is
 * recreated or the app data is wiped, so it never claims a login that no longer exists.
 */
public final class AuthSession {

    private static final ThreadLocal<String> ACCOUNT = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> DIRTY = new ThreadLocal<>();

    private AuthSession() {
    }

    /** The credentials key of the signed-in account, or null when unknown or signed out. */
    public static String account() {
        return ACCOUNT.get();
    }

    public static boolean isSignedInAs(String account) {
        return account != null && account.equals(ACCOUNT.get()) && !isDirty();
    }

    public static void signedInAs(String account) {
        ACCOUNT.set(account);
        DIRTY.remove();
    }

    /** The app state is not trustworthy (e.g. the app died mid-test); the next login must start clean. */
    public static void markDirty() {
        DIRTY.set(Boolean.TRUE);
    }

    public static boolean isDirty() {
        return Boolean.TRUE.equals(DIRTY.get());
    }

    public static void reset() {
        ACCOUNT.remove();
        DIRTY.remove();
    }
}
