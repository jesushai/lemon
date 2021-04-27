package com.lemon.app.admin.service.auto;

import com.lemon.app.admin.entity.SysAdmin;
import com.lemon.app.admin.entity.SysRole;
import com.lemon.app.admin.entity.SysRolePermission;
import com.lemon.app.admin.entity.SysTenant;
import com.lemon.app.admin.mapper.SysAdminMapper;
import com.lemon.app.admin.mapper.SysRoleMapper;
import com.lemon.app.admin.mapper.SysRolePermissionMapper;
import com.lemon.app.admin.mapper.SysTenantMapper;
import com.lemon.app.admin.service.ISysRoleService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.app.core.ModuleBase;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.TimestampUtils;
import com.lemon.framework.util.crypto.password.PasswordEncoderUtil;
import com.lemon.framework.util.sequence.SequenceGenerator;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/24
 */
@Service
@AllArgsConstructor
public class SystemService {

    private final SysTenantMapper tenantMapper;
    private final SysRoleMapper roleMapper;
    private final SysAdminMapper adminMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SequenceGenerator sequenceGenerator;
    private final PermissionService permissionService;

    @Async(BeanNameConstants.CORE_ASYNC_POOL)
    @Transactional
    public void tryInitDefaultTenantAsync() {
        // 是否已经初始化完成
        SysTenant tenant = new SysTenant();
        tenant.setId(0L);
        tenant.setRev(0L);
        tenant.setName("Root Tenant");
        tenant.setDescription("Default Tenant");
        tenant.setActive(true);

        tenant.setModules(AppComponentHolder.getModules().stream()
                .map(ModuleBase::getModule)
                .toArray(String[]::new));

        initTenant(tenant);
    }

    @Transactional
    public void initTenant(SysTenant tenant) {
        SysTenant t = tenantMapper.selectById(tenant.getId());
        // 如果已经存在则不需要初始化
        SysRole role;
        if (null == t) {
            // 保存租户
            if (0L == tenant.getId()) {
                // 默认的租户ID=0，所以需要直接插入
                tenantMapper.insertTenant(tenant);
            } else {
                // 自动生成ID
                tenantMapper.insert(tenant);
            }
            // 超级管理员角色
            role = initAdminRole(tenant);
            // 超级管理员
            initAdminUser(tenant.getId(), role.getId());
        } else {
            role = roleMapper.selectOneByNameAndTenant(tenant.getId(), ISysRoleService.SUPER_ADMIN_ROLE);
            if (null == role) {
                throw new RuntimeException("Not found SuperAdmin role.");
            }
        }
        // 超级管理员权限
        initAdminRolePermissions(tenant.getId(), role.getId());
    }

    /**
     * 超级管理员权限
     */
    private void initAdminRolePermissions(long tenantId, long roleId) {
        if (0L == tenantId) {
            Set<String> permissions = permissionService.getAllPermission("com.lemon.app");

            if (permissions.size() > 0) {
                List<SysRolePermission> rolePermissions = new ArrayList<>();
                Timestamp now = TimestampUtils.now();

                permissions.forEach(p -> {
                    if (rolePermissionMapper.countRolePermission(tenantId, roleId, p) <= 0) {
                        rolePermissions.add(new SysRolePermission()
                                .setId(sequenceGenerator.nextId())
                                .setRoleId(roleId)
                                .setPermission(p)
                                .setCreateBy(0L)
                                .setCreateNameBy(StringUtils.EMPTY)
                                .setCreateTime(now)
                                .setModifiedBy(0L)
                                .setModifiedNameBy(StringUtils.EMPTY)
                                .setModifiedTime(now));
                    }
                });

                if (rolePermissions.size() > 0) {
                    rolePermissionMapper.bulkInsertRolePermission(tenantId, rolePermissions);
                    // 擦除当前租户的权限缓存
//                    rolePermissionService.evictCacheAllPermissionByTenantId(tenantId);
                    // 擦除当前租户所有拥有这个角色的登录人的授权信息缓存
//                    rolePermissionService.evictCacheAuthorizationRealm(tenantId, Sets.newHashSet(roleId));
                }
            }
        }
    }

    /**
     * 超级管理员角色
     */
    private SysRole initAdminRole(SysTenant tenant) {
        SysRole role = new SysRole();
        role.setId(sequenceGenerator.nextId());
        role.setSystem(AppComponentHolder.getSystemName());
        role.setName(ISysRoleService.SUPER_ADMIN_ROLE);
        role.setDescription(StringUtils.EMPTY);
        role.setActive(true);
        role.setCreateBy(0L);
        role.setCreateNameBy(StringUtils.EMPTY);
        role.setCreateTime(TimestampUtils.now());
        role.setModifiedBy(0L);
        role.setModifiedNameBy(StringUtils.EMPTY);
        role.setModifiedTime(role.getCreateTime());
        roleMapper.insertRole(tenant.getId(), role);
        return role;
    }

    /**
     * 超级管理员
     */
    private void initAdminUser(long tenantId, long roleId) {
        adminMapper.insertAdmin(new SysAdmin()
                .setId(sequenceGenerator.nextId())
                .setTenant(tenantId)
                .setDisplay("Administrator")
                .setUsername("admin")
                .setPassword(PasswordEncoderUtil.encryptPassword("admin"))
                .setLastIp(StringUtils.EMPTY)
                .setAvatar(StringUtils.EMPTY)
                .setRoleIds(new Long[]{roleId})
                .setPhone(StringUtils.EMPTY)
                .setIdentity(StringUtils.EMPTY)
                .setEmail(StringUtils.EMPTY)
                .setWechat(StringUtils.EMPTY)
                .setActive(true)
                .setDescription(StringUtils.EMPTY)
                .setCreateBy(0L)
                .setCreateNameBy(StringUtils.EMPTY)
                .setCreateTime(TimestampUtils.now())
                .setModifiedBy(0L)
                .setModifiedNameBy(StringUtils.EMPTY)
                .setModifiedTime(TimestampUtils.now()));
    }

}
