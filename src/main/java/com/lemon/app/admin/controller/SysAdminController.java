package com.lemon.app.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.app.admin.service.ISysAdminService;
import com.lemon.app.constants.AppConstants;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.core.annotation.PermissionDescription;
import com.lemon.framework.db.mp.page.MyPageRequest;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.util.JacksonUtils;
import com.lemon.schemaql.validator.CreateValidatedGroup;
import com.lemon.schemaql.validator.UpdateValidatedGroup;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统人员 前端控制器
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@RestController
@RequestMapping(AppConstants.ADMIN_CONTEXT_ROOT_PATH + "/user")
@RequiredArgsConstructor
@ApiDescription(bizName = "biz!admin|user", resourceType = "Admin")
public class SysAdminController {

    private final ISysAdminService adminService;

    /**
     * 动态查询操作员
     */
    @RequiresPermissions("admin:user:search")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-search")
    @PostMapping("/search")
    @ApiDescription(description = "biz-desc!admin|user-search", args = "#user")
    public IPage<SysAdmin> getAdminUsers(@RequestBody SysAdmin user,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size) {

        IPage<SysAdmin> pageable = MyPageRequest.of(page, size);
        return adminService.getAdminUsersByExample(user, pageable);
    }

    /**
     * 根据ID获取操作员信息
     */
    @RequiresPermissions("admin:user:get")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-get")
    @GetMapping
    @ApiDescription(description = "biz-desc!admin|user-get", args = "#id", resourceId = "#id")
    public SysAdmin getAdminUserById(@RequestParam("id") Long id) {
        return adminService.getAdminUserById(id);
    }

    /**
     * 新建操作员
     */
    @RequiresPermissions("admin:user:create")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-create")
    @PostMapping("/create")
    @ApiDescription(description = "biz-desc!admin|user-create", args = {"#user.username", "#user.display"})
    public SysAdmin create(@Validated(CreateValidatedGroup.class) @RequestBody SysAdmin user) {
        user = adminService.newAdminUser(user);
        user.setRawPassword(null);
        return user;
    }

    /**
     * 修改操作员信息
     */
    @RequiresPermissions("admin:user:update")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-update")
    @PostMapping("/update")
    @ApiDescription(description = "biz-desc!admin|user-update", args = {"#user.id", "#user.username", "#user.display"}, resourceId = "#user.id")
    public SysAdmin update(@Validated(UpdateValidatedGroup.class) @RequestBody SysAdmin user) {
        return adminService.updateAdminUser(user);
    }

    /**
     * 删除操作员
     */
    @RequiresPermissions("admin:user:delete")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-delete")
    @PostMapping("/delete")
    @ApiDescription(description = "biz-desc!admin|user-delete", args = {"#user.id", "#user.username", "#user.display"}, resourceId = "#user.id")
    public void delete(@RequestBody SysAdmin user) {
        if (null == user.getId()) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("id")
                    .throwIt();
        }
        // 管理员不能删除自己账号！
        if (user.getId().equals(AppComponentHolder.getPrincipalSafely().getId())) {
            new ExceptionBuilder<>().code("NNAA-1012").throwIt();
        }

        adminService.deleteAdminUser(user.getId());
    }

    /**
     * 修改操作员密码（非本人）
     */
    @RequiresPermissions("admin:user:update-password")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-user"}, action = "action!admin|user-update-password")
    @PostMapping("/update/pw")
    @ApiDescription(description = "biz-desc!admin|user-update-password", args = "#user.id", resourceId = "#user.id")
    public void updatePassword(@RequestBody SysAdmin user) {
        adminService.updatePassword(user.getId(), user.getRawPassword(), null);
    }

    /**
     * 修改自己的密码不需要菜单权限
     */
    @RequiresAuthentication
    @PostMapping("/update/pw/self")
    @ApiDescription(description = "biz-desc!admin|user-update-password-self")
    public void updatePasswordSelf(@RequestBody String body) {
        String newPassword = JacksonUtils.parseString(body, "newPassword");
        String oldPassword = JacksonUtils.parseString(body, "oldPassword");
        adminService.updatePassword(
                AppComponentHolder.getPrincipalSafely().getId(),
                newPassword,
                oldPassword);
    }

}
