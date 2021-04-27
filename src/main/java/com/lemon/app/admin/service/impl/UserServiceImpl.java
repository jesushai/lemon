package com.lemon.app.admin.service.impl;

import com.lemon.app.admin.mapper.SysAdminMapper;
import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <b>名称：后台管理系统的用户服务实现</b><br/>
 * <b>描述：</b><br/>
 * 用于授权与认证
 *
 * @author hai-zhang
 * @since 2020/6/12
 */
@Service(BeanNameConstants.USER_SERVICE)
@SuppressWarnings({"unchecked", "rawtypes"})
public class UserServiceImpl implements UserService {

    @Autowired
    private SysAdminMapper adminMapper;

    @Override
    public List<User> getUserByIdentificationAndTenant(String identification, Long tenantId) {
        return (List) adminMapper.selectListByUsernameAndTenant(identification, tenantId);
    }

    @Override
    public List<User> getAllUserByIdentification(String identification) {
        return (List) adminMapper.selectListByUsername(identification);
    }

}
