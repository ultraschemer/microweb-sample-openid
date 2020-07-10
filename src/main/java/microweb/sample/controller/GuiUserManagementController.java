package microweb.sample.controller;

import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.view.FtlHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiUserManagementController extends SimpleController {
    private static Template userManagementTemplate;

    static {
        try {
            userManagementTemplate = FtlHelper.getConfiguration().getTemplate("userManagementPage.ftl");
        } catch(Exception e) {
            // This error should not occur - so print it in screen, so the developer can see it, while
            // creating the project
            e.printStackTrace();
        }
    }

    public GuiUserManagementController() {
        super(500, "18478c45-624d-41d1-b284-8aec7520914e");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse httpServerResponse) throws Throwable {
        List<UserData> users = UserManagement.loadUsers(1000, 0);
        users.sort(Comparator.comparing(UserData::getName));
        Map<String, Object> dataRoot = new HashMap<>();
        dataRoot.put("user", routingContext.get("user"));
        dataRoot.put("users", users);
        dataRoot.put("roles", RoleManagement.loadAllRoles());
        routingContext
                .response()
                .putHeader("Content-type", "text/html")
                .putHeader("Cache-Control", "no-cache")
                .end(FtlHelper.processToString(userManagementTemplate, dataRoot));
    }
}
