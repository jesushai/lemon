package com.lemon.app.core;

/**
 * <b>名称：系统模块基本信息标示接口</b><br/>
 * <b>描述：</b><br/>
 * 用于定义每个模块的名命
 *
 * @author hai-zhang
 * @since 2020/6/16
 */
public interface ModuleBase {

    /**
     * 模块名
     */
    String getModule();

    /**
     * 模块基础包路径
     */
    String getBasePackage();
}
