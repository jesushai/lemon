package com.lemon.app.admin.specification;

import com.lemon.framework.domain.specification.AbstractSpecification;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <b>名称：密码强度规范</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPasswordStrengthSpecification extends AbstractSpecification<String[]> {

    public final static UserPasswordStrengthSpecification INSTANCE = new UserPasswordStrengthSpecification();

    /**
     * @param args 第一个参数必须是密码，其余是密码不能包含的字符
     */
    @Override
    public boolean isSatisfiedBy(String[] args) {
        String password = args[0];
        if (StringUtils.isBlank(password)) {
            new ExceptionBuilder<>(NotAcceptableException.class).code("NNA-1004").throwIt();
        }
        if (password.length() < 8 || password.length() > 20) {
            new ExceptionBuilder<>(NotAcceptableException.class).code("NNA-1005").throwIt();
        }
        // TODO:
        // 1. 是否启用强密码规则的开关
        // 2. 强密码验证逻辑，后续添加
        // 2.1 不能连续出现一样的数字、字母
        // 2.2 必须同时包含大小写字母及特殊字符
        // 2.3 不能和登录名一样
        // 2.4 不能包含用户的生日
        for (int i = 1; i < args.length; i++) {
            if (StringUtils.isNotEmpty(args[i]) && password.toLowerCase().contains(args[i].toLowerCase())) {
                new ExceptionBuilder<>(NotAcceptableException.class).code("NNA-1006").args(args[i]).throwIt();
            }
        }
        // ...
        return true;
    }
}
