package microweb.sample.domain;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.domain.Configuration;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.json.JsonObject;
import microweb.sample.domain.error.FinishAuthenticationConsentException;
import microweb.sample.domain.error.LogoffException;
import okhttp3.*;

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

    public static void logoff(String refreshToken, String accessToken, BiConsumer<JsonObject, StandardException> callResult) {
        new Thread(() -> {
            try {
                FormBody body = new FormBody.Builder()
                        .add("client_id", Configuration.read("keycloak client application"))
                        .add("client_secret", Configuration.read("keycloak client application secret"))
                        .add("refresh_token", refreshToken)
                        .build();
                Request clientRequest = new Request.Builder()
                        .url(Configuration.read("server backend resource") + "/auth/reamls/" +
                                Configuration.read("keycloak client application") +
                                "/protocol/openid-connect/logout")
                        .post(body)
                        .addHeader("Authorization", "Bearer: " + accessToken)
                        .build();
                try (Response response = client.newCall(clientRequest).execute()) {
                    if (response.code() <= 299) {
                        if(response.code()!=204) {
                            JsonObject res = new JsonObject(Objects.requireNonNull(response.body()).string());
                            callResult.accept(res, null);
                        } else {
                            callResult.accept(new JsonObject(),null);
                        }
                    } else {
                        callResult.accept(null, new FinishAuthenticationConsentException("Unable to finish client authentication consent: " +
                                Objects.requireNonNull(response.body()).string()));
                    }
                } catch(Exception e) {
                    callResult.accept(null, new LogoffException("Unable to perform logoff.", e));
                }
            } catch(Throwable t) {
                callResult.accept(null, new LogoffException("Unable to perform logoff.", t));
            }
        }).start();
    }

    public static void logoff(String refreshToken, BiConsumer<JsonObject, StandardException> callResult) {
        new Thread(() -> {
            // TODO: implement refresh logic
            // As is shown in the next PHP code:
            // <?php
            //     require_once './common_credentials.php';

            //     // Request form data:
            //     $data = array (
            //             'client_id' => $client_id,
            //             'client_secret' => $client_secret,
            //             'refresh_token' => $_POST['refresh_token'],
            //             'grant_type' => 'refresh_token'
            //     );

            //     $params = '';
            //     foreach($data as $key=>$value) {
            //         $params .= $key.'='.$value.'&';
            //     }
            //     $params = trim($params, '&');

            //     // Make the Microweb backend call, to get the Access Token and all other session information:
            //     $url = $openid_server_address . '/auth/realms/' . $realm . '/protocol/openid-connect/token';
            //     $ch = curl_init();
            //     curl_setopt($ch, CURLOPT_URL, $url);
            //     curl_setopt($ch, CURLOPT_POST, 1);
            //     curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
            //     curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Return data instead printing directly in Browser
            //     curl_setopt($ch, CURLOPT_CONNECTTIMEOUT , 7); //Timeout after 7 seconds
            //     curl_setopt($ch, CURLOPT_USERAGENT , "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            //     curl_setopt($ch, CURLOPT_HEADER, 0);

            //     $result = curl_exec($ch);

            //     // Format response:
            //     header('Content-Type: application/json; charset=utf-8');
            //     if(curl_errno($ch)) { //catch if curl error exists and show it
            //         http_response_code(500);
            //         echo json_encode(array('error' => curl_error($ch)));
            //     } else {
            //         $info = curl_getinfo($ch);
            //         http_response_code($info['http_code']);
            //         if($info['http_code'] != 204) {
            //             echo json_encode(array('result' => json_decode($result), 'info' => $info));
            //         }
            //     }

            //     // Close the curl object:
            //     curl_close($ch);
            // ?>

            callResult.accept(null, new StandardException("", 0, "Unimplemented"));
        }).start();
    }
}
