package com.lemon.app.core;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * <b>名称：系统信息</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/16
 */
@Component
@Primary
public class AdminModuleInfo implements ModuleBase {

    @Override
    public String getModule() {
        return "lemon-admin";
    }

    @Override
    public String getBasePackage() {
        return "com.lemon.app.admin";
    }

}
