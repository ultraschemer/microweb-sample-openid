package microweb.sample.controller;

import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.controller.bean.ImageUserLinkData;
import microweb.sample.domain.ImageManagement;

public class ImageUserLinkController extends SimpleController {
    public ImageUserLinkController() {
        super(500, "c57ff7a7-a255-4bf9-b6e3-dd923f2bffa9");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        ImageUserLinkData linkData = Json.decodeValue(context.getBodyAsString(), ImageUserLinkData.class);
        Validator.ensure(linkData);

        ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());
        imageManagement.linkToUser(context.get("user"), linkData.getImageId(), linkData.getUserId(), linkData.getAlias());

        response.setStatusCode(204).end();
    }
}
