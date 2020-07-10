package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageListingData;
import microweb.sample.view.FtlHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultHomePageController extends SimpleController {
    // C.1: Create a static instance of the default home page template variable, to store and cache it:
    private static Template homePageTemplate = null;

    // C.2: Initialize the template defined above, suitably:
    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    // C.3: Define the default controller constructor:
    public DefaultHomePageController() {
        super(500, "a37c914b-f737-4a73-a226-7bd86baac8c3");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        // Define homepage data model:
        Map<String, Object> homepageDataRoot = new HashMap<>();

        // Load user cookie:
        HttpServerRequest request = routingContext.request();
        Cookie authorizationToken = request.getCookie("Microweb-Access-Token");

        // Populate Homepage data model:
        if(authorizationToken != null) {
            // Get user data:
            User u = AuthManagement.authorize(authorizationToken.getValue());

            // Load images, if they are available for this user:
            ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), routingContext.vertx());
            List<ImageListingData> imageListingData = imageManagement.list(u);

            // Load users, to assign images to them:
            List<UserData> users = UserManagement.loadUsers(1000, 0);
            users.sort(Comparator.comparing(UserData::getName));

            homepageDataRoot.put("logged", true);
            homepageDataRoot.put("user", u);
            homepageDataRoot.put("images", imageListingData);
            homepageDataRoot.put("users", users);
        } else {
            homepageDataRoot.put("logged", false);
        }

        // C.4: In the controller evaluation routine, render the template:
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .putHeader("Cache-Control", "no-cache")
                .end(FtlHelper.processToString(homePageTemplate, homepageDataRoot));
    }
}