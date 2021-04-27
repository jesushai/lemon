package com.lemon.app.admin.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.app.admin.dto.RolePermissionDTO;
import com.lemon.app.admin.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 查询指定租户可以使用的所有权限（租户内所有角色的）
     *
     * @return 租户内所有角色的权限
     */
    @InterceptorIgnore(tenantLine = "true")
    List<RolePermissionDTO> selectAllRolePermissionByTenant(@Param("tenant") Long tenant);

    @InterceptorIgnore(tenantLine = "true")
    int countRolePermission(@Param("tenant") Long tenantId, @Param("roleId") Long roleId, @Param("permission") String permission);

    /**
     * 批量删除指定角色ID下的权限
     */
    @InterceptorIgnore(tenantLine = "true")
    int removeAllByRoleIdAndPermissions(@Param("tenant") Long tenant, @Param("roleId") Long roleId, @Param("permissions") Collection<String> permissions);

    int bulkInsertRolePermission(@Param("tenant") Long tenantId, @Param("permissions") List<SysRolePermission> rolePermissions);

    /**
     * 查询所有权限
     *
     * @return 所有权限
     */
    List<String> selectAllPermission();

    List<RolePermissionDTO> selectPermissionsByRoleId(@Param("roleId") Long roleId);

}
