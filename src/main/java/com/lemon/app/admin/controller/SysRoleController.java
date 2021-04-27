package com.lemon.app.admin.controller;

import com.lemon.app.admin.dto.RolePermissionDTO;
import com.lemon.app.admin.entity.SysRole;
import com.lemon.app.admin.service.ISysRolePermissionService;
import com.lemon.app.admin.service.ISysRoleService;
import com.lemon.app.constants.AppConstants;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.core.annotation.PermissionDescription;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.schemaql.validator.CreateValidatedGroup;
import com.lemon.schemaql.validator.UpdateValidatedGroup;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@RestController
@RequestMapping(AppConstants.ADMIN_CONTEXT_ROOT_PATH + "/role")
@RequiredArgsConstructor
@ApiDescription(bizName = "biz!admin|role", resourceType = "Role")
public class SysRoleController {

    private final ISysRoleService roleService;
    private final ISysRolePermissionService rolePermissionService;

    /**
     * 动态查询角色列表（当前租户）
     */
    @RequiresAuthentication
    @PostMapping("/search")
    public List<SysRole> getRoles(@RequestBody SysRole role) {
        return roleService.getRolesByExample(role);
    }

    /**
     * 新建角色
     */
    @RequiresPermissions("admin:role:create")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-role"}, action = "action!admin|role-create")
    @PostMapping("/create")
    @ApiDescription(description = "biz-desc!admin|role-create", args = {"#role.name"})
    public SysRole createRole(@Validated(CreateValidatedGroup.class) @RequestBody SysRole role) {
        if (null == role) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("role")
                    .throwIt();
        }
        return roleService.createRole(role);
    }

    /**
     * 修改角色信息
     */
    @RequiresPermissions("admin:role:update")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-role"}, action = "action!admin|role-update")
    @PostMapping("/update")
    @ApiDescription(description = "biz-desc!admin|role-update", args = {"#role.id"})
    public SysRole updateRole(@Validated(UpdateValidatedGroup.class) @RequestBody SysRole role) {
        if (null == role) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("role")
                    .throwIt();
        }
        return roleService.updateRole(role);
    }


    /**
     * 获取角色内的权限
     */
    @RequiresAuthentication
    @GetMapping("/permission")
    public List<RolePermissionDTO> getPermissionByRole(@RequestParam("role") Long roleId) {
        return rolePermissionService.getPermissionByRole(roleId);
    }

    /**
     * 给角色分配权限
     */
    @RequiresPermissions("admin:role:assign")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-role"}, action = "action!admin|role-assign")
    @PostMapping("/assign")
    @ApiDescription(description = "biz-desc!admin|role-assign", args = {"#roleId", "#permissions"})
    public void assignPermissions(@RequestParam("role") Long roleId, @RequestBody Set<String> permissions) {
        rolePermissionService.batchSavePermission(roleId, permissions);
    }

    /**
     * 移除角色内的权限
     */
    @RequiresPermissions("admin:role:remove")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-role"}, action = "action!admin|role-remove")
    @PostMapping("/remove")
    @ApiDescription(description = "biz-desc!admin|role-remove", args = {"#roleId", "#permissions"})
    public void removePermissions(@RequestParam("role") Long roleId, @RequestBody Set<String> permissions) {
        rolePermissionService.removeRolePermission(roleId, permissions);
    }

}
