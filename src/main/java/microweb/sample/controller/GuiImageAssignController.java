package microweb.sample.controller;

import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;

import java.util.UUID;

public class GuiImageAssignController extends SimpleController {
    public GuiImageAssignController() {
        super(500, "c8fd6b8c-b0ec-4331-bf84-20382e616bf5");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();
        UUID imageId = UUID.fromString(request.getParam("id"));
        UUID userId = UUID.fromString(request.getFormAttribute("userId"));
        String alias = request.getFormAttribute("alias");


        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());
        imageManagement.linkToUser(context.get("user"), imageId, userId, alias);

        // Redirect home:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0")
                .setStatusCode(303)
                .end();
    }
}
