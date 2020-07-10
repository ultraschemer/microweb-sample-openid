package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.PermissionManagement;

public class PermissionControlFilter extends SimpleController {
    public PermissionControlFilter() {
        super(500, "dfa4afdd-9314-48a4-aa17-30dde0dbeda0");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = context.request();
        if(PermissionManagement.evaluatePermission(context.get("user"), request.path(), request.method().toString())) {
            // Continue processing
            context.next();
        } else {
            // Reached a restricted path - which is forbidden.
            response.setStatusCode(403)
                    .putHeader("Content-type", "text/html")
                    .end("<html><body><h1>Forbidden</h1></body></html>");
        }
    }
}
