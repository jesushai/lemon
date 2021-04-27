package com.lemon.app.admin.service.impl;

import com.lemon.app.admin.dto.RolePermissionDTO;
import com.lemon.app.admin.mapper.SysMenuMapper;
import com.lemon.app.admin.service.ISysRolePermissionService;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/12
 */
@Service(BeanNameConstants.PERMISSION_SERVICE)
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final ISysRolePermissionService rolePermissionService;
    private final SysMenuMapper menuMapper;

    public PermissionServiceImpl(@Lazy ISysRolePermissionService rolePermissionService,
                                 SysMenuMapper menuMapper) {
        this.rolePermissionService = rolePermissionService;
        this.menuMapper = menuMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId) {

        if (roleIds == null || roleIds.length <= 0) {
            return Collections.EMPTY_SET;
        }

        List<Long> roleIdList = Arrays.asList(roleIds);
        List<RolePermissionDTO> permissions = rolePermissionService.getAllPermissionByTenantId(tenantId);

        Set<String> result = new HashSet<>();
        permissions.stream()
                .filter(p -> roleIdList.contains(p.getRoleId()))
                .forEach(p -> result.add(p.getPermission()));
        return result;
    }

    @Override
    public Set<Permission> getAllPermission(Long tenantId) {
        return menuMapper.selectPermissionBySystem("", tenantId);
    }

}
