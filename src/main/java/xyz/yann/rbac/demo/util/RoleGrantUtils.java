package xyz.yann.rbac.demo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import xyz.yann.rbac.demo.domain.PermissionEntity;
import xyz.yann.rbac.demo.domain.RoleGrant;

public final class RoleGrantUtils {

    private RoleGrantUtils() {
    }

    public static List<RoleGrant> fromPermissions(Collection<PermissionEntity> permissions) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (PermissionEntity permission : permissions) {
            if (permission.getResource() == null) {
                continue;
            }
            String resourceKey = permission.getResource().getResourceKey();
            if (resourceKey == null) {
                continue;
            }
            grouped.computeIfAbsent(resourceKey, key -> new ArrayList<>());
            List<String> actions = grouped.get(resourceKey);
            String actionCode = permission.getActionCode();
            if (actionCode != null && !actions.contains(actionCode)) {
                actions.add(actionCode);
            }
        }
        return grouped.entrySet()
                .stream()
                .map(entry -> new RoleGrant(entry.getKey(), List.copyOf(entry.getValue())))
                .toList();
    }
}
