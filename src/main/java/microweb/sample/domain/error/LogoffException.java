package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class LogoffException extends StandardException {
    public LogoffException(String message, Throwable cause) {
        super("31ae70ca-35fc-4ba6-9b61-aafcf0078bb5", 500, message, cause);
    }
}
