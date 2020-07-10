package microweb.sample.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageManagementSaveException extends StandardException {
    public ImageManagementSaveException(String message, Exception cause) {
        super("9880d7d9-9496-4324-a605-f8d19ac3788d", 500, message, cause);
    }
}
