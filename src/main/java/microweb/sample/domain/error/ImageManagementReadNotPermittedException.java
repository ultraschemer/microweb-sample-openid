package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementReadNotPermittedException extends StandardException {
    public ImageManagementReadNotPermittedException(String message) {
        super("b2d710c4-95d8-4f43-9f13-bf1810f36f36", 500, message);
    }
}
