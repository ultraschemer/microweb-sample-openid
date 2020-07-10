package microweb.sample.controller;

public class AuthorizationFilter extends com.ultraschemer.microweb.controller.AuthorizationFilter {
    public AuthorizationFilter() {
        super();
        this.addUnfilteredPath("/");
        this.addUnfilteredPath("/v0");

        // Add this, to release the login form to any unauthenticated user:
        this.addUnfilteredPath("/v0/gui-user-login");
    }
}
