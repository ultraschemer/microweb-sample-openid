package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class FinishAuthenticationConsentException extends StandardException {
    public FinishAuthenticationConsentException(String message) {
        super("32d8f687-8940-418f-bc56-8a587e61af9a", 500, message);
    }
    public FinishAuthenticationConsentException(String message, Exception cause) {
        super("32d8f687-8940-418f-bc56-8a587e61af9a", 500, message, cause);
    }
}
