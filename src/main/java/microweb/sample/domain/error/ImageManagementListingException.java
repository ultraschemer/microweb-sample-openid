package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementListingException extends StandardException {
    public ImageManagementListingException(String message, Throwable cause) {
        super("b751fabe-7134-4b80-95f3-94cd5ed01b07", 500, message, cause);
    }
}
