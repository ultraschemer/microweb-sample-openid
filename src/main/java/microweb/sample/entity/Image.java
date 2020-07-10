package microweb.sample.entity;

import com.ultraschemer.microweb.persistence.Createable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "image")
public class Image extends Createable {
    @Column(name = "name")
    private String name;

    @Column(name = "base64data")
    private String base64data;

    @Column(name="owner_user_id")
    private UUID ownerUserId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase64data() {
        return base64data;
    }

    public void setBase64data(String base64data) {
        this.base64data = base64data;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
