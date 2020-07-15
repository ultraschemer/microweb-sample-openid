package microweb.sample.controller;

import com.ultraschemer.microweb.domain.Configuration;
import com.ultraschemer.microweb.proxy.CentralAuthorizedServerProxyController;

public class PostgRESTRedirectionController extends CentralAuthorizedServerProxyController {
    public PostgRESTRedirectionController() {
        super(500, "150e7454-2754-4270-bf0a-a70958cf17ea");
    }

    @Override
    protected String getServerAddress() throws Throwable {
        String postgrestAddress = Configuration.read("PostgREST address");
        if(postgrestAddress.equals("")) {
            return "http://localhost:9580";
        }
        return postgrestAddress;
    }
}
