package microweb.sample.controller;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.controller.bean.ImageCreationData;
import microweb.sample.controller.error.ImageCreationException;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageRegistrationData;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ImageCreateController extends SimpleController {
    public ImageCreateController() {
        super(500, "cb989df8-acf1-46a5-bf9b-75879ebb4abe");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        new Thread(() -> {
            asyncEvaluation(500, "d0b8fdc8-4614-4122-ac50-c779108baec7", context, () -> {
                ImageCreationData imageCreationData = Json.decodeValue(context.getBodyAsString(), ImageCreationData.class);
                Validator.ensure(imageCreationData);

                if(!imageCreationData.getName().toLowerCase().matches("^.*\\.(jpg|jpeg|png|bmp|tiff|svg|ico|gif|webp)$")) {
                    throw new ValidationException("Unexpected format for an image. Use files with these extensions: jpg, jpeg, png, bmp, tiff, svg, ico, gif or webp.");
                }

                File dir = new File("file-uploads");

                if(!dir.exists()) {
                    dir.mkdir();
                }

                File f = new File("file-uploads" + File.separator + UUID.randomUUID().toString());

                if(f.createNewFile()) {
                    try (FileOutputStream fOut = new FileOutputStream(f)) {
                        fOut.write(Base64.getDecoder().decode(imageCreationData.getBase64FileRepresentation()));
                        fOut.flush();
                    } catch(Exception e) {
                        // Remove processed file:
                        f.delete();
                        throw e;
                    }

                    // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
                    ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

                    // Create the input bean:
                    User u = context.get("user");
                    ImageRegistrationData imageRegistrationData = new ImageRegistrationData();
                    imageRegistrationData.setUserId(u.getId());
                    imageRegistrationData.setImageFileName(f.getAbsolutePath());
                    imageRegistrationData.setName(imageCreationData.getName());

                    // Save file:
                    imageManagement.save(imageRegistrationData, (UUID uuid, StandardException e) -> {
                        // Process results using default method asyncEvaluation, which treats any error
                        // and ensures HTTP evaluation finalization:
                        asyncEvaluation(500, "d7056121-3bf2-4f72-92f4-7b0435954572", context, () -> {
                            // Removed processed file:
                            f.delete();

                            // Raises exception, in the case of error - Microweb will deal with it suitably:
                            if (e != null) {
                                throw e;
                            }

                            JsonObject res = new JsonObject();
                            res.put("id", uuid.toString());
                            response.putHeader("Content-type", "application/json")
                                    .setStatusCode(200)
                                    .end(res.encode());
                        });
                    });
                } else {
                    throw new ImageCreationException("Unable to create and persist image file.");
                }
            });
        }).start();
    }
}
