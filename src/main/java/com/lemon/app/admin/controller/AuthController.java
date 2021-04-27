package com.lemon.app.admin.controller;

import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.app.admin.service.ISysAdminService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.shiro.TenantUserAuthorizingRealm;
import com.lemon.framework.auth.shiro.TenantUsernamePasswordToken;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.core.annotation.ApiLimit;
import com.lemon.framework.core.enums.LimitType;
import com.lemon.framework.exception.AuthenticationException;
import com.lemon.framework.exception.AuthorizationException;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.log.LogTypeEnum;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.IpUtils;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 名称：身份认证控制器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/24
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@ApiDescription(bizName = "biz!admin|login", resourceType = "Admin")
public class AuthController {

    private final ISysAdminService adminService;
    private final TenantUserAuthorizingRealm tenantUserAuthorizingRealm;
    private final RoleService roleService;
    private final PermissionService permissionService;

    /*
     *  { username : value, password : value, tenantId: value }
     * 10秒钟限制尝试登录2次
     */
    @ApiLimit(key = "AdminLogin", period = 10, count = 2, name = "Admin Login Request", prefix = "limit", limitType = LimitType.CUSTOMER)
    @ApiDescription(type = LogTypeEnum.SYSTEM, description = "biz-desc!admin|user-login", args = {"#user['username']", "#user['tenantId']"})
    @PostMapping("/login")
    public Object login(@RequestBody Map<String, Object> user, HttpServletRequest request) {
        String username = (String) user.get("username");
        String password = (String) user.get("password");
        long tenantId;
        if (user.containsKey("tenantId")) {
            try {
                tenantId = ((Number) user.get("tenantId")).longValue();
            } catch (Exception e) {
                tenantId = 0L;
            }
        } else {
            tenantId = 0L;
        }

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            new ExceptionBuilder<>(AuthenticationException.class).throwIt();
        }

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(new TenantUsernamePasswordToken(username, password, tenantId));

        currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();
        admin.setLastIp(IpUtils.getRequestHost(request));
        admin.setLastLogin(TimestampUtils.now());
        adminService.updateById(admin);

        // userInfo
//        Map<String, Object> adminInfo = new HashMap<>();
//        adminInfo.put("nickName", admin.getUsername());
//        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", admin);
        return result;
    }

    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();
        LoggerUtils.info(log, "退出");
        // 清除Principal缓存
        tenantUserAuthorizingRealm.clearCache();
        // 登录Subject
        currentUser.logout();
        return Result.ok();
    }

    @RequiresAuthentication
    @GetMapping("/info")
    @ApiLimit(key = "AdminInfo", period = 10, count = 2, name = "Get Principal", prefix = "limit", limitType = LimitType.PRINCIPAL_KEY)
    public Object info() {
        SysAdmin user = AppComponentHolder.getPrincipalSafely();

        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getUsername());
        data.put("avatar", user.getAvatar());

        Long[] roleIds = user.getRoleIds();
        Set<String> roles = roleService.getNamesByIds(roleIds);
        Set<String> permissions = permissionService.getPermissionsByRoleIds(roleIds, user.getTenant());
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions, user.getTenant()));
        return data;
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return Result.ok();
    }

    @GetMapping("/401")
    public Object page401() {
        return Result.error(new ExceptionBuilder<>(AuthenticationException.class).build());
    }

    @GetMapping("/403")
    public Object page403() {
        return Result.error(new ExceptionBuilder<>(AuthorizationException.class).build());
    }

    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions, Long tenantId) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "com.lemon";
            List<Permission> systemPermissions = permissionService.getPermissions(basicPackage, tenantId);
            for (Permission permission : systemPermissions) {
                String perm = permission.getId()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            if (api != null) {
                apis.add(api);
            }

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();
            }
        }
        return apis;
    }

}
