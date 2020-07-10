package microweb.sample.controller;

import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class GuiCreateUserController extends SimpleController {
    public GuiCreateUserController() {
        super(500, "63ccb2ee-3c99-4b20-91d4-bb521f4945dd");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        // Get form data:
        CreateUserData userData = new CreateUserData();
        userData.setName(request.getFormAttribute("name").toLowerCase());
        userData.setAlias(userData.getName());
        userData.setPassword(request.getFormAttribute("password"));
        userData.setPasswordConfirmation(request.getFormAttribute("passConfirmation"));
        userData.setGivenName(request.getFormAttribute("givenName"));
        userData.setFamilyName(request.getFormAttribute("familyName"));

        Validator.ensure(userData);

        UserManagement.registerSimpleUser(userData, request.getFormAttribute("role"));

        // Redirect to users management interface:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0/gui-user-management")
                .setStatusCode(303)
                .end();
    }
}
