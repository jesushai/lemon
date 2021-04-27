package com.lemon.app.core;

import com.lemon.app.constants.AppConstants;

/**
 * <b>名称：系统模块基本信息标示接口</b><br/>
 * <b>描述：</b><br/>
 * 用于定义每个模块的名命
 *
 * @author hai-zhang
 * @since 2020/6/16
 */
public interface SystemBase {

    /**
     * 系统名
     */
    default String getSystem() {
        return AppConstants.SYSTEM_NAME;
    }

}
