package microweb.sample.domain;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.domain.Configuration;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.json.JsonObject;
import microweb.sample.domain.error.FinishAuthenticationConsentException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Objects;
import java.util.function.BiConsumer;

public class PermissionManagement {
    private static OkHttpClient client = new OkHttpClient();

    public static void finishLogin(String state, String sessionState, String code,
                                   BiConsumer<JsonObject, StandardException> callResult) {
        // Since this method uses an internal REST call recursively, call it asynchronously, to avoid to block
        // Vert.X event queue (A Vert.X future can be used here):
        new Thread(() -> {
            try {
                Request clientRequest = new Request.Builder()
                        .url(Configuration.read("server backend resource") +
                                "/v0/finish-consent?" +
                                "state=" + state + "&" +
                                "session_state=" + sessionState + "&" +
                                "code=" + code + "&" +
                                "redirect_uri=" + Configuration.read("keycloak client redirect uri") + "&" +
                                "client_secret=" + Configuration.read("keycloak client application secret") + "&" +
                                "client_id=" + Configuration.read("keycloak client application"))
                        .build();

                try (Response response = client.newCall(clientRequest).execute()) {
                    if (response.code() <= 299) {
                        JsonObject res = new JsonObject(Objects.requireNonNull(response.body()).string());

                        // Locate user from returned data, evaluating the permission of an ALWAYS permitted resource:
                        User u = CentralUserRepositoryManagement.evaluateResourcePermission("GET", "/v0/logoff",
                                "Bearer " + res.getString("access_token"));
                        res.put("Microweb-User-Id", u.getId().toString());
                        res.put("Microweb-User-Name", u.getName());
                        res.put("Microweb-Central-Control-User-Id", u.getCentralControlId().toString());

                        callResult.accept(res, null);
                    } else {
                        callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent: " +
                                Objects.requireNonNull(response.body()).string()));
                    }
                } catch (Exception e) {
                    callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent.", e));
                }
            } catch (Exception e) {
                callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent", e));
            }
        }).start();
    }
}
