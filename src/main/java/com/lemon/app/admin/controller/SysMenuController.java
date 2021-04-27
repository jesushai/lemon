package com.lemon.app.admin.controller;

import com.lemon.app.admin.dto.MenuDto;
import com.lemon.app.admin.service.ISysMenuService;
import com.lemon.app.constants.AppConstants;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.core.annotation.PermissionDescription;
import com.lemon.schemaql.validator.UpdateValidatedGroup;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 系统菜单 前端控制器
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@RestController
@RequestMapping(AppConstants.ADMIN_CONTEXT_ROOT_PATH + "/menu")
@RequiredArgsConstructor
@ApiDescription(bizName = "biz!admin|menu", resourceType = "Menu")
public class SysMenuController {

    private final ISysMenuService menuService;

    /**
     * 查询租户有权访问的所有菜单
     */
    @RequiresPermissions("admin:menu:search")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-menu"}, action = "action!admin|menu-search")
    @GetMapping("/search")
    public List<MenuDto> selectTenantMenu() {
        return menuService.selectTenantMenu();
    }

    /**
     * 修改菜单信息
     */
    @RequiresPermissions("admin:menu:update")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-menu"}, action = "action!admin|menu-update")
    @PostMapping("/update")
    @ApiDescription(description = "biz-desc!admin|menu-update", args = {"#menu.code", "#menu.display"})
    public MenuDto updateMenu(@Validated(UpdateValidatedGroup.class) @RequestBody MenuDto menu) {
        return menuService.updateMenu(menu);
    }

    /**
     * 菜单禁用
     */
    @RequiresPermissions("admin:menu:disable")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-menu"}, action = "action!admin|menu-disable")
    @PostMapping("/disable")
    @ApiDescription(description = "biz-desc!admin|menu-disable", args = {"#id"})
    public void disableMenu(@RequestParam("id") Long id) {
        menuService.updateMenu(new MenuDto().setId(id).setActive(false));
    }

    /**
     * 菜单启用
     */
    @RequiresPermissions("admin:menu:enable")
    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-menu"}, action = "action!admin|menu-enable")
    @PostMapping("/enable")
    @ApiDescription(description = "biz-desc!admin|menu-enable", args = {"#id"})
    public void enableMenu(@RequestParam("id") Long id) {
        menuService.updateMenu(new MenuDto().setId(id).setActive(true));
    }

//    /**
//     * 菜单删除，此功能不提供
//     */
//    @RequiresPermissions("admin:menu:delete")
//    @PermissionDescription(menu = {"menu!admin|admin", "menu!admin|admin-menu"}, action = "action!admin|menu-delete")
//    @PostMapping("/delete")
//    @ApiDescription(description = "biz-desc!admin|menu-delete", args = {"#id"})
//    public void deleteMenu(@RequestParam("id") Long id) {
//        menuService.deleteMenu(id);
//    }
}
