package microweb.sample.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class GuiAssignRoleController extends SimpleController {
    public GuiAssignRoleController() {
        super(500, "0d75c820-7650-41cd-be63-01c91bf2e4ea");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();

        UUID userId =  UUID.fromString(request.getFormAttribute("userId"));
        String role = request.getFormAttribute("role");

        // Set user role:
        UserManagement.setRoleToUser(userId, role);

        // Redirect to users management interface:
        response.putHeader("Content-type", "text/html")
                .putHeader("Location", "/v0/gui-user-management")
                .setStatusCode(303)
                .end();
    }
}
