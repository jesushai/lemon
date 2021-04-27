package com.lemon.app.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.app.admin.mapper.SysAdminMapper;
import com.lemon.app.admin.service.ISysAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.app.admin.specification.AdminSaveSpecification;
import com.lemon.app.admin.specification.UserPasswordStrengthSpecification;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.util.crypto.password.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统人员 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements ISysAdminService {

    @Override
    public IPage<SysAdmin> getAdminUsersByNameLike(String name, IPage<SysAdmin> pageable) {
        return baseMapper.selectPage(
                pageable,
                new LambdaQueryWrapper<SysAdmin>()
                        .like(SysAdmin::getDisplay, name));
    }

    @Override
    public IPage<SysAdmin> getAdminUsersByExample(SysAdmin example, IPage<SysAdmin> pageable) {
        return baseMapper.selectPageByExample(pageable, example);
    }

    @Override
    public SysAdmin getAdminUserById(Long id) {
        SysAdmin user = baseMapper.selectById(id);
        if (null == user) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("NNAA-1009")
                    .args("" + id)
                    .throwIt();
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysAdmin newAdminUser(SysAdmin user) {
        user.setId(null);
        AdminSaveSpecification.INSTANCE.isSatisfiedBy(user);
        // 密码加密
        String rawPassword = user.getRawPassword();
        String encode = PasswordEncoderUtil.encryptPassword(rawPassword);
        user.setPassword(encode);
        int n = baseMapper.insert(user);
        if (n <= 0) {
            new ExceptionBuilder<>().code("NNAA-1008").throwIt();
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, String newPassword, String oldPassword) {
        if (null == id) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("id")
                    .throwIt();
        }
        // 是否修改本人密码
        boolean myself = id.equals(AppComponentHolder.getPrincipalSafely().getId());

        // 获取用户从DB中
        SysAdmin user = getAdminUserById(id);
        // 如果是本人修改则检查原密码
        if (myself && (!StringUtils.hasText(oldPassword) || !PasswordEncoderUtil.matches(oldPassword, user.getPassword()))) {
            new ExceptionBuilder<>().code("NNAA-1011").throwIt();
        }
        // 密码一样则提示
        if (myself && oldPassword.equals(newPassword)) {
            new ExceptionBuilder<>().code("NNAA-1013").throwIt();
        }

        // 验证新密码强度
        UserPasswordStrengthSpecification.INSTANCE.isSatisfiedBy(
                new String[]{newPassword, user.getUsername()});
        // 密码加密并更新
        String encode = PasswordEncoderUtil.encryptPassword(newPassword);

        // 新建实体，避免全字段更新
        SysAdmin entity = new SysAdmin();
        entity.setId(id);
        entity.setRev(user.getRev()); // 乐观锁必须带着
        entity.setPassword(encode);
        int n = baseMapper.updateById(entity);
        if (n <= 0) {
            new ExceptionBuilder<>().code("NNAA-1008").throwIt();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysAdmin updateAdminUser(SysAdmin user) {

        // 不允许修改密码，应该使用专门的修改密码api
        user.setRawPassword(null);
        user.setPassword(null);
        // 不允许修改username
        user.setUsername(null);
        // 检查有效性
        AdminSaveSpecification.INSTANCE.isSatisfiedBy(user);

        // 读取乐观锁并复制
        SysAdmin dbUser = getAdminUserById(user.getId());
        user.setRev(dbUser.getRev());

        int n = baseMapper.updateById(user);
        if (n <= 0) {
            new ExceptionBuilder<>().code("NNAA-1008").throwIt();
        }
        user = baseMapper.selectById(user.getId());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminUser(Long id) {
        if (null == id) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("id")
                    .throwIt();
        }
        baseMapper.deleteById(id);
    }
}
