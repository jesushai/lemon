package com.lemon.app.admin.specification;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.app.admin.mapper.SysAdminMapper;
import com.lemon.framework.domain.specification.AbstractSpecification;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.util.spring.SpringContextUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <b>名称：管理员保存规则</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminSaveSpecification extends AbstractSpecification<SysAdmin> {

    public final static AdminSaveSpecification INSTANCE = new AdminSaveSpecification();

    @Override
    public boolean isSatisfiedBy(SysAdmin admin) {
        if (null == admin) {
            new ExceptionBuilder<>(NotAcceptableException.class).code("NNA-1001").throwIt();
        }

        SysAdminMapper mapper = SpringContextUtils.getBean(SysAdminMapper.class);
        if (null == admin.getId()) {
            // 新增检查
            // 1. 密码不能为空，不能小于8位，不能太简单，不能包含用户名
            UserPasswordStrengthSpecification.INSTANCE.isSatisfiedBy(
                    new String[]{admin.getRawPassword(), admin.getUsername()});
            // 2. 检查是否已经存在
            int cnt = mapper.selectCount(new LambdaQueryWrapper<SysAdmin>()
                    .eq(SysAdmin::getUsername, admin.getUsername()));
            if (cnt > 0) {
                new ExceptionBuilder<>(NotAcceptableException.class)
                        .code("NNA-1007")
                        .args(admin.getUsername())
                        .throwIt();
            }
        } else {
            // 更新检查
            // 1. 是否存在，并检查状态
            Long userId = admin.getId();
            SysAdmin user = mapper.selectById(userId);
            if (null == user) {
                new ExceptionBuilder<>(NotFoundException.class)
                        .code("NNA-1009")
                        .args("" + userId)
                        .throwIt();
            }
            if (!user.getActive()) {
                new ExceptionBuilder<>()
                        .code("NNA-1010")
                        .args("" + userId)
                        .throwIt();
            }

            // 2. 修改了密码，则需要检查密码强度
            if (StringUtils.isNotEmpty(admin.getRawPassword())) {
                UserPasswordStrengthSpecification.INSTANCE.isSatisfiedBy(
                        new String[]{admin.getRawPassword(), admin.getUsername()});
            }
            // 3. 修改了姓名，则检查不能为空
            if (null != admin.getDisplay()) {
                if (StringUtils.isBlank(admin.getDisplay())) {
                    new ExceptionBuilder<>(NotAcceptableException.class).code("NNA-1003").throwIt();
                }
            }
        }

        // latest. TODO: 修改了电话、身份证、email，则检查是否合法

        return true;
    }
}
