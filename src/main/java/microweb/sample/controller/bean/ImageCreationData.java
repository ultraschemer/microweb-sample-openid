package microweb.sample.controller.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;

public class ImageCreationData implements Serializable {
    @NotNull
    @NotEmpty
    String base64FileRepresentation;

    @NotNull
    @NotEmpty
    String name;

    public String getBase64FileRepresentation() {
        return base64FileRepresentation;
    }

    public void setBase64FileRepresentation(String base64FileRepresentation) {
        this.base64FileRepresentation = base64FileRepresentation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
