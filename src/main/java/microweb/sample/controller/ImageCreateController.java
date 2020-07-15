package microweb.sample.controller;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.controller.bean.ImageCreationData;
import microweb.sample.domain.ImageManagement;

import javax.xml.bind.ValidationException;

public class ImageCreateController extends CentralUserRepositoryAuthorizedController {
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

                // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
                ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

                // Create the input bean:
                User u = context.get("user");
                imageManagement.saveBase64ImageRepresentation(imageCreationData.getBase64FileRepresentation(),
                        imageCreationData.getName(), u.getId());
            });
        }).start();
    }
}
