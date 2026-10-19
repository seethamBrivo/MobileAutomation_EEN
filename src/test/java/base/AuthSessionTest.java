package base;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class AuthSessionTest {

    @AfterMethod(alwaysRun = true)
    public void clean() {
        AuthSession.reset();
    }

    @Test
    public void startsSignedOut() {
        assertNull(AuthSession.account());
        assertFalse(AuthSession.isSignedInAs("reseller"));
    }

    @Test
    public void tracksTheSignedInAccount() {
        AuthSession.signedInAs("reseller");
        assertEquals(AuthSession.account(), "reseller");
        assertTrue(AuthSession.isSignedInAs("reseller"));
        assertFalse(AuthSession.isSignedInAs("enduser"));
        assertFalse(AuthSession.isSignedInAs(null));
    }

    @Test
    public void dirtyStateIsNotTrustedUntilTheNextLogin() {
        AuthSession.signedInAs("reseller");
        AuthSession.markDirty();
        assertTrue(AuthSession.isDirty());
        assertFalse(AuthSession.isSignedInAs("reseller"));
        AuthSession.signedInAs("reseller");
        assertFalse(AuthSession.isDirty());
        assertTrue(AuthSession.isSignedInAs("reseller"));
    }

    @Test
    public void resetForgetsEverything() {
        AuthSession.signedInAs("reseller");
        AuthSession.markDirty();
        AuthSession.reset();
        assertNull(AuthSession.account());
        assertFalse(AuthSession.isDirty());
    }

    @Test
    public void stateIsPerThread() throws Exception {
        AuthSession.signedInAs("reseller");
        AtomicReference<String> seenByOtherThread = new AtomicReference<>("unset");
        Thread other = new Thread(() -> seenByOtherThread.set(AuthSession.account()));
        other.start();
        other.join();
        assertNull(seenByOtherThread.get());
        assertEquals(AuthSession.account(), "reseller");
    }
}
