package pages;

/**
 * Sign-in stopped at the identity provider's "Account verification" (security code) step. A test cannot finish it,
 * and repeating it only risks locking the account, so the account is treated as unusable for the rest of the class.
 */
public class AccountVerificationRequiredException extends IllegalStateException {

    public AccountVerificationRequiredException(String message) {
        super(message);
    }
}
