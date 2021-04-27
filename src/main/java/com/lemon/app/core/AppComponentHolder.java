package com.lemon.app.core;

import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.exception.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-6-29
 */
@Component
public class AppComponentHolder {

    private static AuthenticationService authenticationService;
    private static SystemBase system;
    private static Collection<ModuleBase> modules;

    public AppComponentHolder(AuthenticationService authenticationService,
                              SystemBase system,
                              Collection<ModuleBase> modules) {
        AppComponentHolder.authenticationService = authenticationService;
        AppComponentHolder.system = system;
        AppComponentHolder.modules = modules;
    }

    public static SysAdmin getPrincipalSafely() {
        SysAdmin admin = (SysAdmin) authenticationService.getPrincipal();
        if (null == admin) {
            throw new AuthenticationException();
        }
        return admin;
    }

    public static String getSystemName() {
        return system.getSystem();
    }

    public static Collection<ModuleBase> getModules() {
        return modules;
    }

}
