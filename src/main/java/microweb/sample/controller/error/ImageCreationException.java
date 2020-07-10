package microweb.sample.controller.error;

import com.ultraschemer.microweb.error.StandardException;

public class ImageCreationException extends StandardException {
    public ImageCreationException(String message) {
        super("1dc95621-f346-4914-98a9-9a29b9b2d054", 500, message);
    }
}
