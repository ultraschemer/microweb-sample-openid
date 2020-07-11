package microweb.sample.domain;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.validation.Validator;
import io.vertx.core.Vertx;
import microweb.sample.domain.bean.ImageListingData;
import microweb.sample.domain.bean.ImageRegistrationData;
import microweb.sample.domain.error.*;
import microweb.sample.entity.Image;
import microweb.sample.entity.User_Image;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.PersistenceException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ImageManagement extends StandardDomain {
    public ImageManagement(SessionFactory factory, Vertx vertx) {
        super(factory, vertx);
    }

    public void save(ImageRegistrationData imageRegistrationData, BiConsumer<UUID, StandardException> resultHandler) {
        // A.1: Assure the input parameter is respecting its contract:
        try {
            Validator.ensure(imageRegistrationData, 500);
        } catch(StandardException e) {
            resultHandler.accept(null, e);
        }

        // A.2: Isolate the entire operation in a new thread, since it can be time and resource consuming:
        new Thread(() -> {
            try {
                // A.3: Read file:
                File file = new File(imageRegistrationData.getImageFileName());
                byte[] fEncoded = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));

                // A.4: Convert it to String:
                String base64contents = new String(fEncoded, StandardCharsets.US_ASCII);

                try(Session session = openTransactionSession()) {
                    // A.5: Save image in database
                    Image img = new Image();
                    img.setBase64data(base64contents);
                    img.setName(imageRegistrationData.getName());
                    img.setOwnerUserId(imageRegistrationData.getUserId());
                    session.persist(img);

                    // A.6: Commit saved data:
                    session.getTransaction().commit();

                    // A.7: Finish operation:
                    resultHandler.accept(img.getId(), null);
                }
            } catch(Exception e) {
                resultHandler.accept(null, new ImageManagementSaveException("Unable to persist image to user." , e));
            }
        }).start();
    }

    // Save Base64Image representation
    public void saveBase64ImageRepresentation(String base64contents, String name, UUID userId) throws StandardException {
        try(Session session = openTransactionSession()) {
            // A.5: Save image in database
            Image img = new Image();
            img.setBase64data(base64contents);
            img.setName(name);
            img.setOwnerUserId(userId);
            session.persist(img);

            // A.6: Commit saved data:
            session.getTransaction().commit();
        } catch(Exception e) {
            throw new ImageManagementSaveBase64RepresentationError("Unable to save image data", e);
        }
    }

    // List images from user:
    public List<ImageListingData> list(User user) throws StandardException {
        try(Session session = openTransactionSession()) {
            List<ImageListingData> res = new LinkedList<>();

            List<User_Image> accessibleImageList =
                    session.createQuery("from User_Image where userId = :uid", User_Image.class)
                            .setParameter("uid", user.getId())
                            .list();
            HashMap<UUID, User_Image> userImageMap = new HashMap<>();
            accessibleImageList.forEach((i) -> userImageMap.put(i.getImageId(), i));

            List<Image> allImages;
            if(accessibleImageList.size() > 0) {
                allImages = session.createQuery("from Image where id in :iid or ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameterList("iid", accessibleImageList.stream().map(User_Image::getImageId).collect(Collectors.toList()))
                        .setParameter("oid", user.getId())
                        .list();
            } else {
                allImages = session.createQuery("from Image where ownerUserId = :oid order by ownerUserId", Image.class)
                        .setParameter("oid", user.getId())
                        .list();
            }

            UUID ownerUserId = null;
            String ownerUserName = null;
            for(Image i: allImages) {
                if(!i.getOwnerUserId().equals(ownerUserId)) {
                    User u = session.createQuery("from User where id = :uid", User.class)
                            .setParameter("uid", i.getOwnerUserId())
                            .getSingleResult();
                    ownerUserId = u.getId();
                    ownerUserName = u.getName();
                }

                ImageListingData imageListingData = new ImageListingData();
                imageListingData.setId(i.getId());
                imageListingData.setName(i.getName());
                imageListingData.setOwnerId(ownerUserId);
                imageListingData.setOwnerName(ownerUserName);
                imageListingData.setCreatedAt(i.getCreatedAt());
                if(userImageMap.containsKey(i.getId())) {
                    User_Image ui = userImageMap.get(i.getId());
                    imageListingData.setAlias(ui.getAlias());
                }
                res.add(imageListingData);
            }

            // Set return data in creation order:
            res.sort(Comparator.comparing(ImageListingData::getCreatedAt));

            return res;
        } catch(PersistenceException pe) {
            throw new ImageManagementListingException("Unable to list images.", pe);
        }
    }

    // Read, synchronously the Image object:
    public Image read(User user, UUID imageId) throws StandardException {
        // Implement image reading routines here.
        try (Session session = openTransactionSession()){
            Image image = session.createQuery("from Image where id = :iid", Image.class)
                    .setParameter("iid", imageId)
                    .getSingleResult();

            if(!image.getOwnerUserId().equals(user.getId())) {
                // Verify if the user has access to such image:
                List<User_Image> userImageList = session.createQuery("from User_Image where imageId = :iid and userId = :uid",
                        User_Image.class).setParameter("iid", imageId).setParameter("uid", user.getId())
                        .list();
                if(userImageList.size() == 0) {
                    throw new ImageManagementReadNotPermittedException("User has no permission to read such image.");
                }
            }
            return image;
        } catch(Exception e) {
            throw new ImageManagementReadException("Unable to read image.", e);
        }
    }

    // Link a user to an image, so that user can see the image on his/her user interface.
    public void linkToUser(User owner, UUID imageId, UUID userId, String imageAlias) throws StandardException {
        try (Session session = openTransactionSession()){
            Image img = session.createQuery("from Image where id = :iid and ownerUserId = :oid", Image.class)
                    .setParameter("iid", imageId)
                    .setParameter("oid", owner.getId())
                    .getSingleResult();
            User_Image userImage = new User_Image();
            userImage.setUserId(userId);
            userImage.setImageId(img.getId());
            userImage.setAlias(imageAlias);
            session.persist(userImage);
            session.getTransaction().commit();
        } catch(Exception e) {
            throw new ImageManagementImageUserLinkingException(e.getLocalizedMessage(), e);
        }
    }

    // Read and decode image data asynchronously.
    public void readAndDecode(User user, UUID imageId, TriConsumer<Image, byte[], StandardException> resultHandler) {
        new Thread(() -> {
            try {
                Image img = read(user, imageId);
                byte []imgByteRepresentation = Base64.getDecoder().decode(img.getBase64data());
                resultHandler.accept(img, imgByteRepresentation, null);
            } catch(StandardException e) {
                resultHandler.accept(null, null, e);
            } catch(Exception e) {
                resultHandler.accept(null, null, new ImageManagementReadException(e.getLocalizedMessage(), e));
            }
        }).start();
    }
}
