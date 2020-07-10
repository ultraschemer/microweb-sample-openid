package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLogoffProcessController extends SimpleController {
    private static Template homePageTemplate = null;

    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLogoffProcessController() {
        super(500, "eb474551-42d5-4452-be4f-4875d525b993");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        String token = routingContext.getCookie("Microweb-Access-Token").getValue();

        // Perform logoff here:
        AuthManagement.unauthorize(token);
        Map<String, Object> homepageDataRoot = new HashMap<>();
        homepageDataRoot.put("logged", false);

        // Render home page again:
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));

    }
}
