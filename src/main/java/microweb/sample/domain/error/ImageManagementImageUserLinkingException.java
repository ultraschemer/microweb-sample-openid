package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementImageUserLinkingException extends StandardException {
    public ImageManagementImageUserLinkingException(String message, Exception cause) {
        super("a2baffbb-149c-4f99-8b1c-85e500798f7c", 500, message, cause);
    }
}
