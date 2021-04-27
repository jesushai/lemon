package com.lemon.app.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lemon.app.admin.dto.RolePermissionDTO;
import com.lemon.app.admin.entity.SysRolePermission;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 获取租户下的所有权限代码，支持缓存
     *
     * @return 租户下的所有权限
     */
    List<RolePermissionDTO> getAllPermissionByTenantId(Long tenantId);

    /**
     * 获取角色拥有的权限列表
     *
     * @return 角色拥有的权限列表
     */
    List<RolePermissionDTO> getPermissionByRole(Long roleId);

//    /**
//     * 清除指定租户的缓存，除此之外没有任何代码实现
//     */
//    void evictCacheAllPermissionByTenantId(Long tenantId);

//    /**
//     * 擦除指定租户指定角色的授权域缓存
//     * 因为一旦角色的权限发生变化，redis中的授权信息就过时了
//     *
//     * @param tenantId 租户
//     * @param ids      改变了权限的角色id
//     */
//    void evictCacheAuthorizationRealm(Long tenantId, Set<Long> ids);

    /**
     * 给角色分配权限
     *
     * @param roleId 角色id
     * @param names  分配的权限
     */
    void batchSavePermission(Long roleId, Set<String> names);

//    /**
//     * 将权限指定给多个角色
//     */
//    void batchSavePermission(String name, Set<Long> roleIds);
//
//    /**
//     * 删除角色权限关联
//     */
//    void removeRolePermission(Long id);

    /**
     * 批量删除角色权限关联
     *
     * @param roleId 角色id
     * @param names  删除的权限
     */
    void removeRolePermission(Long roleId, Set<String> names);

}
