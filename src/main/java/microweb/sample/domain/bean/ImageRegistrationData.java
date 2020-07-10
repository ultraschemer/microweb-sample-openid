package microweb.sample.domain.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class ImageRegistrationData implements Serializable {
    @NotNull
    UUID userId;

    @NotNull
    @NotEmpty
    String imageFileName;

    @NotNull
    @NotEmpty
    String name;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
