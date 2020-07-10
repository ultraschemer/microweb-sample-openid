package microweb.sample.domain;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.entity.Role;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.utils.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionManagement {
    /**
     * Simple permission evaluation. Just verify if the path and method are inside a specific group. If so, return True.
     * False, otherwise.
     * @param u The user being evaluated
     * @param path The path being evaluated
     * @return True, if the user has permission to access path. False, otherwise.
     */
    public static boolean evaluatePermission(User u, String path, String method) {
        try {
            // If user is given by controller, than authorization has been evaluated.
            if(u != null) {
                // In the case of a valid authorization, it's necessary to evaluate permissions:
                List<Role> roleList = UserManagement.loadRolesFromUser(u.getId());
                Set<String> roleSet = roleList.stream().map(Role::getName).collect(Collectors.toSet());
                Set<String> restrictRoleSet = new HashSet<>();
                restrictRoleSet.add("root");
                restrictRoleSet.add("user-manager");
                roleSet.retainAll(restrictRoleSet);

                if (roleSet.size() > 0) {
                    // All routes are permitted
                    return true;
                }

                // Block all restricted paths:
                return  !Resource.resourceIsEquivalentToPath("GET /v0/gui-user-management#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/gui-user/:id/role#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/gui-user#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("POST /v0/user#", path, method) &&
                        !Resource.resourceIsEquivalentToPath("GET /v0/user/:userIdOrName#", path, method);
            } else {
                // Any route without required authorization is automatically permitted:
                return true;
            }
        } catch(Exception e) {
            return false;
        }
    }
}
