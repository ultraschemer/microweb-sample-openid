package microweb.sample.controller;

import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;

public class GuiCreateUserController extends CentralUserRepositoryAuthorizedController {
    public GuiCreateUserController() {
        super(500, "63ccb2ee-3c99-4b20-91d4-bb521f4945dd");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        if(request.getHeader("Content-Type").trim().toLowerCase().startsWith("application/json")) {
            CreateUserData userData = Json.decodeValue(context.getBodyAsString(), CreateUserData.class);
            Validator.ensure(userData);

            CentralUserRepositoryManagement.registerUser(context.get("user"), userData, Collections.singletonList(request.getFormAttribute("role")));

            response.setStatusCode(204).end();
        } else {
            // Get form data:
            CreateUserData userData = new CreateUserData();
            userData.setName(request.getFormAttribute("name").toLowerCase());
            userData.setAlias(userData.getName());
            userData.setPassword(request.getFormAttribute("password"));
            userData.setPasswordConfirmation(request.getFormAttribute("passConfirmation"));
            userData.setGivenName(request.getFormAttribute("givenName"));
            userData.setFamilyName(request.getFormAttribute("familyName"));

            Validator.ensure(userData);

            CentralUserRepositoryManagement.registerUser(context.get("user"), userData, Collections.singletonList(request.getFormAttribute("role")));

            // Redirect to users management interface:
            response.putHeader("Content-type", "text/html")
                    .putHeader("Location", "/v0/gui-user-management")
                    .setStatusCode(303)
                    .end();
        }
    }
}
