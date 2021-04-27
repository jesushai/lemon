package com.lemon.app.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemon.app.admin.dto.MenuDto;
import com.lemon.app.admin.entity.SysMenu;

import java.util.List;

/**
 * <p>
 * 系统菜单 服务类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 读取应用模块内的权限（菜单与按钮），随后初始化到DB中
     *
     * @param system      当前系统
     * @param module      当前模块
     * @param basePackage 包路径
     */
    void initMenuAsync(String system, String module, String basePackage);

    /**
     * 查询当前租户可以使用的所有菜单
     */
    List<MenuDto> selectTenantMenu();

    MenuDto updateMenu(MenuDto menu);

    void deleteMenu(Long id);
}
