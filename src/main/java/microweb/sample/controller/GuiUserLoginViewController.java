package microweb.sample.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLoginViewController extends SimpleController {
    private static Template loginFormTemplate;

    static {
        try {
            loginFormTemplate = FtlHelper.getConfiguration().getTemplate("loginForm.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLoginViewController() {
        super(500, "85c2c7d9-eab9-4b6e-9ebd-271966722124");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        Map<String, Object> loginMessageData = new HashMap<>();
        loginMessageData.put("error", false);
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .end(FtlHelper.processToString(loginFormTemplate, loginMessageData));
    }
}
