package microweb.sample;

import com.ultraschemer.microweb.controller.FinishConsentController;
import com.ultraschemer.microweb.controller.OtherUsersController;
import com.ultraschemer.microweb.controller.UserPasswordUpdateController;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.proxy.CentralAuthorizedRegisteredReverseProxy;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.StaticHandler;
import microweb.sample.controller.*;

// 1. Specialize WebAppVerticle:
public class App extends WebAppVerticle {
    static {
        // 2. Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() throws Exception {
        getRouter().route("/static/*").handler(StaticHandler.create());

        // Register controllers:

        // Finish login authentication:
        registerController(HttpMethod.GET, "/v0/finish-login", new FinishLoginController());

        // Default finish consent call:
        registerController(HttpMethod.GET, "/v0/finish-consent", new FinishConsentController());

        // User access controllers:
        registerController(HttpMethod.GET, "/v0/gui-user-login", new GuiUserLoginViewController());
        registerController(HttpMethod.POST, "/v0/gui-user-login", new GuiUserLoginProcessController());
        registerController(HttpMethod.GET, "/v0/gui-user-logoff", new GuiUserLogoffProcessController());

        // Image manipulation:
        registerController(HttpMethod.POST, "/v0/gui-image/:id/assign", new GuiImageAssignController());
        registerController(HttpMethod.GET, "/v0/gui-image/:id/raw", new GuiImageRawDataController());
        registerController(HttpMethod.POST, "/v0/gui-image", new GuiImageCreationController());

        // User management:
        registerController(HttpMethod.GET, "/v0/gui-user-management", new GuiUserManagementController());
        registerController(HttpMethod.POST, "/v0/gui-user/:id/role", new GuiAssignRoleController());
        registerController(HttpMethod.POST, "/v0/gui-user", new GuiCreateUserController());

        // REST API calls:
        registerController(HttpMethod.POST, "/v0/user", new GuiCreateUserController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.PATCH, "/v0/user/:id/password", new UserPasswordUpdateController());
        registerController(HttpMethod.POST, "/v0/image", new ImageCreateController());
        registerController(HttpMethod.PUT, "/v0/image/:id/link", new ImageUserLinkController());

        // Default system home page handling:
        registerController(HttpMethod.GET, "/v0", new DefaultHomePageController());

        // Remove this path, because it will be handled by the reverse proxy:
        // Register calls to external microservices:
        // registerController(HttpMethod.GET, "/image", new PostgRESTRedirectionController());

        // At last step, register the route "GET /", which is the most generic one:
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());

        CentralAuthorizedRegisteredReverseProxy proxy = new CentralAuthorizedRegisteredReverseProxy(9080);

        // KeyCloak and Microweb own paths aren't evaluated by the reverse proxy,
        // since they have their own Permission control:
        proxy.registerPath("^\\/auth.*$", "localhost:8080");
        proxy.registerPath("^\\/v0.*$", "localhost:48080");

        // Add any generic search path to PostgREST, with the exception of "/", and enable Permission filtering on them:
        proxy.registerPath("^\\/.+$", "localhost:9580", true);

        // "/", being the most generic address, continue to be handled by this application:
        proxy.registerPath("^\\/$", "localhost:48080");
        proxy.run();
    }

    public static void main(String[] args) {
        // 7. Create the Application Vertx instance:
        Vertx vertx = Vertx.vertx();

        // 8. Deploy the WebAppVerticle:
        vertx.deployVerticle(new App());
    }
}
