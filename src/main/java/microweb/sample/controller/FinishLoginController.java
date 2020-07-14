package microweb.sample.controller;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class FinishLoginController extends SimpleController {
    public FinishLoginController() {
        super(500, "44154235-fd79-4487-9092-56e9e280e2d5");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        try {
            HttpServerRequest request = context.request();
            JsonObject authenticationData = PermissionManagement.finishLogin(request.getParam("state"),
                    request.getParam("session_state"),
                    request.getParam("code"));

            // Vert.X has a bug which prevents setting multiple cookies simultaneously, so we create a simple page
            // to set the cookies and, then, redirect itself to home page:
            response.setStatusCode(200)
                    .putHeader("Content-type", "text/html")
                    .end("<html><head>" +
                            "<title>Microweb login</title>" +
                            "<head>" +
                            "<body>Logging in..." +
                            "<script language=\"javascript\">"+
                            "document.cookie = \"Microweb-Access-Token=" + "" + "; path=/;\";" +
                            "document.cookie = \"Microweb-User-Id=" + "" + "; path=/;\";" +
                            "document.cookie = \"Microweb-Refresh-Token=" + "" + "; path=/;\";" +
                            "document.cookie = \"Microweb-User-Name=" + "" + "; path=/;\";" +
                            "window.location.replace(\"/v0\");"+
                            "</script>"+
                            "</body>" +
                            "</html>");
        } catch(Exception e) {
            response.setStatusCode(401)
                    .putHeader("Content-type", "text/html")
                    .end("<html><body>Authorization error:<br/>" +
                            Throwables.getStackTraceAsString(e) + "</body></html>");
        }
    }
}
