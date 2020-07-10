package microweb.sample.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.bean.UserLoginData;
import microweb.sample.view.FtlHelper;

import java.util.HashMap;
import java.util.Map;

public class GuiUserLoginProcessController extends SimpleController {
    private static Template loginFormTemplate = null;

    static {
        try {
            loginFormTemplate = FtlHelper.getConfiguration().getTemplate("loginForm.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserLoginProcessController() {
        super(500, "7f65217b-a95e-4b0c-8161-5ab116b49dea");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = routingContext.request();

        try {
            // D.1: Validate input data:
            UserLoginData userLoginData =
                    new UserLoginData(request.getFormAttribute("name"), request.getFormAttribute("password"));
            Validator.ensure(userLoginData);

            // D.2: Transform data and call business rule to perform login:
            AuthenticationData authenticationData = new AuthenticationData();
            authenticationData.setName(userLoginData.getName());
            authenticationData.setPassword(userLoginData.getPassword());

            // D.3: Business call:
            AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);

            // Redirect home:
            response.putHeader("Set-Cookie", "Microweb-Access-Token=" + authorizationData.getAccessToken())
                    .putHeader("Content-type", "text/html")
                    .putHeader("Location", "/v0")
                    .setStatusCode(303)
                    .end();
        } catch(StandardException e) {
            // D.5: Business call failure, return to login form, but with error message:
            Map<String, Object> loginMessageData = new HashMap<>();
            loginMessageData.put("errorMessage", e.getLocalizedMessage());
            loginMessageData.put("error", true);
            response.putHeader("Content-type", "text/html")
                    .setStatusCode(401)
                    .end(FtlHelper.processToString(loginFormTemplate, loginMessageData));
        }
    }
}