package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementReadException extends StandardException {
    public ImageManagementReadException(String message, Exception cause) {
        super("efa3115f-c742-410c-a908-5eb2bda6de24", 500, message, cause);
    }
}
