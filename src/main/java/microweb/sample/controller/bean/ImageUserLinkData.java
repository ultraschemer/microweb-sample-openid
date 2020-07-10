package microweb.sample.controller.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class ImageUserLinkData implements Serializable {
    @NotNull
    UUID userId;

    @NotNull
    UUID imageId;

    @NotNull
    @NotEmpty
    String alias;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
