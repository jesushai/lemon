package com.lemon.app.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lemon.app.admin.entity.SysAdmin;

/**
 * <p>
 * 系统人员 服务类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface ISysAdminService extends IService<SysAdmin> {

    IPage<SysAdmin> getAdminUsersByNameLike(String name, IPage<SysAdmin> pageable);

    IPage<SysAdmin> getAdminUsersByExample(SysAdmin example, IPage<SysAdmin> pageable);

    SysAdmin getAdminUserById(Long id);

    SysAdmin newAdminUser(SysAdmin user);

    /**
     * 修改操作员员密码
     *
     * @param id          操作员ID
     * @param newPassword 新密码
     * @param oldPassword 原密码（修改自己的密码时必须提供）
     */
    void updatePassword(Long id, String newPassword, String oldPassword);

    SysAdmin updateAdminUser(SysAdmin user);

    void deleteAdminUser(Long id);
}
