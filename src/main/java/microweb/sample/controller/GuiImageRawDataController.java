package microweb.sample.controller;

import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.entity.Image;

import java.util.UUID;

public class GuiImageRawDataController extends SimpleController {
    public GuiImageRawDataController() {
        super(500, "303c2bc7-cd1e-4570-b0c3-5a25053a8d1b");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        //
        // Load binary image and return it to caller:
        //

        // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

        // Load and decode image asynchronously, to maintain system performance:
        imageManagement.readAndDecode(context.get("user"), UUID.fromString(request.getParam("id")),
                (Image image, byte[]contents, StandardException error) ->
                {   // Call asyncEvaluation, so Microweb ensures the HTTP call will be finished suitably:
                    asyncEvaluation(500, "", context, () -> {
                        if(error!= null) {
                            throw error;
                        }

                        // Format image return data:
                        String [] imageNameParts = image.getName().split("\\.");
                        String imageExtension = imageNameParts[imageNameParts.length-1];
                        if(imageExtension.equals("jpg")) {
                            imageExtension = "jpeg";
                        }
                        Buffer b = Buffer.buffer(contents);

                        // Return it to caller:
                        response.putHeader("Content-Type", "image/" + imageExtension).end(b);
                    });
                });
    }
}
