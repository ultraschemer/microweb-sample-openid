package microweb.sample.controller;

import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.CentralUserRepositoryAuthorizedController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class GuiUserLogoffProcessController extends CentralUserRepositoryAuthorizedController {
    public GuiUserLogoffProcessController() {
        super(500, "eb474551-42d5-4452-be4f-4875d525b993");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        String token = context.getCookie("Microweb-Access-Token").getValue();
        String refreshToken = context.getCookie("Microweb-Refresh-Token").getValue();

        PermissionManagement.logoff(refreshToken, token, (JsonObject j, StandardException se) -> {
            asyncEvaluation(500, "8ada30f6-e400-4994-9c2e-cd41df80439f", context, () -> {
                // Delete all cookies:
                response.setStatusCode(200)
                        .end("<html><head>" +
                                "<title>Microweb login</title>" +
                                "<head>" +
                                "<body>Logging in..." +
                                "<script language=\"javascript\">" +
                                "document.cookie = \"Microweb-Access-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-User-Id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-Central-Control-User-Id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-Refresh-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "document.cookie = \"Microweb-User-Name=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\";" +
                                "window.location.replace(\"/v0\");" +
                                "</script>" +
                                "</body>" +
                                "</html>");
            });
        });
    }
}
