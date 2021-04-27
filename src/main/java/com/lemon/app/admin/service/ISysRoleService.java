package com.lemon.app.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemon.app.admin.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface ISysRoleService extends IService<SysRole> {

    String SUPER_ADMIN_ROLE = "SuperAdmin";

    /**
     * 获取可用的指定ID的角色
     *
     * @param roleIds 角色ID列表
     * @return 可用的角色
     */
    List<SysRole> getRolesByIds(Long[] roleIds);

    /**
     * 动态查询角色列表
     */
    List<SysRole> getRolesByExample(SysRole role);

    /**
     * 新建角色
     */
    SysRole createRole(SysRole role);

    /**
     * 修改角色
     */
    SysRole updateRole(SysRole role);

}
