package com.lemon.app.configuration.auth;

import com.lemon.framework.auth.shiro.ShiroKey;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.stereotype.Component;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Component(BeanNameConstants.SHIRO_KEY)
public class ShiroKeyImpl implements ShiroKey {

    @Override
    public String rememberMeEncryptKey() {
        return "lemon_shiro_key";
    }

    @Override
    public String loginTokenKey() {
        return "X-Lemon-Token";
    }

    @Override
    public String referencedSessionIdSource() {
        return "Stateless Request";
    }
}
