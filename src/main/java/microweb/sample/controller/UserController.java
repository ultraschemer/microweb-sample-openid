package microweb.sample.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class UserController extends CentralUserRepositoryAuthorizedController {
    public UserController() {
        super(500, "db7ff50d-ead4-4daf-8dec-caa2e3521f9");
    }

    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        User user = (User)routingContext.get("user");
        UserData userData = UserManagement.loadUserBySecureId(user.getId().toString());
        response.setStatusCode(200).end(Json.encode(userData));
    }
}
