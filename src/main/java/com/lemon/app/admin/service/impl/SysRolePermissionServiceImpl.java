package com.lemon.app.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.app.admin.dto.RolePermissionDTO;
import com.lemon.app.admin.entity.SysRole;
import com.lemon.app.admin.entity.SysRolePermission;
import com.lemon.app.admin.mapper.SysRoleMapper;
import com.lemon.app.admin.mapper.SysRolePermissionMapper;
import com.lemon.app.admin.service.ISysRolePermissionService;
import com.lemon.app.admin.service.ISysRoleService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Service
@Transactional(readOnly = true)
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

    private final SysRoleMapper sysRoleMapper;

    public SysRolePermissionServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<RolePermissionDTO> getAllPermissionByTenantId(Long tenantId) {
        return baseMapper.selectAllRolePermissionByTenant(tenantId);
    }

    @Override
    public List<RolePermissionDTO> getPermissionByRole(Long roleId) {
        return baseMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSavePermission(Long roleId, Set<String> names) {
        Long tenantId = AppComponentHolder.getPrincipalSafely().getTenant();
        List<SysRolePermission> permissions = new ArrayList<>();

        // 租户允许的所有授权
        List<RolePermissionDTO> all = getAllPermissionByTenantId(tenantId);
        if (CollectionUtils.isEmpty(all)) {
            return;
        }

        // 只添加允许的授权，没有授权给租户则报错
        names.forEach(name -> {
            if (all.stream().noneMatch(r -> r.getPermission().equals(name))) {
                new ExceptionBuilder<>().code("NNAR-1101").args(name).throwIt();
            }
            // 已经有权限就不再增加了
            if (baseMapper.countRolePermission(tenantId, roleId, name) <= 0) {
                SysRolePermission permission = new SysRolePermission();
                permission.setRoleId(roleId);
                permission.setPermission(name);
                permissions.add(permission);
            }
        });

        if (permissions.size() > 0) {
            this.saveBatch(permissions);
        }

//        if (permissions.size() > 0 && this.saveBatch(permissions)) {
//            self.evictCacheAllPermissionByTenantId(tenantId);
//            self.evictCacheAuthorizationRealm(tenantId, Sets.newHashSet(roleId));
//        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void batchSavePermission(String name, Set<Long> roleIds) {
//        List<RolePermission> permissions = new ArrayList<>(roleIds.size());
//
//        roleIds.forEach(roleId -> {
//            RolePermission permission = new RolePermission();
//            permission.setRoleId(roleId);
//            permission.setPermission(name);
//            permissions.add(permission);
//        });
//
//        if (this.saveBatch(permissions)) {
//            Long tenantId = AppComponentHolder.getPrincipalSafe().getTenant();
//            self.evictCacheAllPermissionByTenantId(tenantId);
//            self.evictCacheAuthorizationRealm(tenantId, roleIds);
//        }
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void removeRolePermission(Long id) {
//        RolePermission permission = baseMapper.selectById(id);
//        if (permission != null && this.removeById(id)) {
//            Long tenantId = AppComponentHolder.getPrincipalSafe().getTenant();
//            self.evictCacheAllPermissionByTenantId(tenantId);
//            self.evictCacheAuthorizationRealm(tenantId, Sets.newHashSet(permission.getRoleId()));
//        }
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolePermission(Long roleId, Set<String> names) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (null == role) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("NNAR-1005")
                    .args(roleId)
                    .throwIt();
        }
        if (role.getName().equals(ISysRoleService.SUPER_ADMIN_ROLE)) {
            new ExceptionBuilder<>()
                    .code("NNAR-1102")
                    .throwIt();
        }

        if (CollectionUtils.isNotEmpty(names)) {
            Long tenantId = AppComponentHolder.getPrincipalSafely().getTenant();
            baseMapper.removeAllByRoleIdAndPermissions(tenantId, roleId, names);
//            if (baseMapper.removeAllByRoleIdAndPermissions(tenantId, roleId, names) > 0) {
//                self.evictCacheAllPermissionByTenantId(tenantId);
//                self.evictCacheAuthorizationRealm(tenantId, Sets.newHashSet(roleId));
//            }
        }
    }

}
