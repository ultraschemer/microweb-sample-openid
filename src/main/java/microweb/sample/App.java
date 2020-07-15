package microweb.sample;

import com.ultraschemer.microweb.controller.*;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.proxy.RegisteredReverseProxy;
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
        registerController(HttpMethod.GET, "/", new DefaultHomePageController());

        RegisteredReverseProxy proxy = new RegisteredReverseProxy(9080);
        proxy.registerPath("^\\/auth.*$", "localhost:8080");
        proxy.registerPath("^\\/v0.*$", "localhost:48080" );
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
