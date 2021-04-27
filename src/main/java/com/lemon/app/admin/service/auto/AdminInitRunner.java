package com.lemon.app.admin.service.auto;

import com.lemon.app.admin.service.ISysMenuService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/24
 */
@Component
@Order(0)
@Slf4j
public class AdminInitRunner implements ApplicationRunner {

    private final ISysMenuService menuService;
    private final SystemService systemService;

    public AdminInitRunner(ISysMenuService menuService,
                           SystemService systemService) {
        this.menuService = menuService;
        this.systemService = systemService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 【异步】过滤admin module的菜单与功能，并同步到db中。
        try {
            AppComponentHolder.getModules().forEach(module -> menuService.initMenuAsync(
                    AppComponentHolder.getSystemName(),
                    module.getModule(),
                    module.getBasePackage())
            );
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw e;
        }

        // 2. 【异步】初始化默认的租户，同时初始化租户的超级管理员及角色
        try {
            systemService.tryInitDefaultTenantAsync();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw e;
        }
    }
}