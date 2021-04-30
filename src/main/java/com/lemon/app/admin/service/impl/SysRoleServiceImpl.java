package com.lemon.app.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.app.admin.entity.SysRole;
import com.lemon.app.admin.mapper.SysRoleMapper;
import com.lemon.app.admin.service.ISysRoleService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Service(BeanNameConstants.ROLE_SERVICE)
@Transactional(readOnly = true)
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService, RoleService {

    @Override
    public Set<String> getNamesByIds(Long[] roleIds) {
        Set<String> result = new HashSet<>();
        if (null != roleIds && roleIds.length > 0) {
            getRolesByIds(roleIds).stream()
                    .map(SysRole::getName)
                    .forEach(result::add);
        }
        return result;
    }

    @Override
    public List<SysRole> getRolesByIds(Long[] roleIds) {
        return baseMapper.selectListByIdInAndActiveIsTrue(roleIds);
    }

    @Override
    public List<SysRole> getRolesByExample(SysRole role) {
        return baseMapper.selectListByExample(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole createRole(SysRole role) {
        // 检查角色是否已经存在
        if (baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getSystem, AppComponentHolder.getSystemName())
                .eq(SysRole::getName, role.getName())) > 0) {
            new ExceptionBuilder<>()
                    .code("NNA-1144")
                    .args(role.getName()).throwIt();
        }

        role.setSystem(AppComponentHolder.getSystemName());
        role.setActive(true);
        if (baseMapper.insert(role) <= 0) {
            new ExceptionBuilder<>().code("NNA-1145").throwIt();
        }

        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole updateRole(SysRole role) {
        // 检查是否存在
        SysRole db = baseMapper.selectById(role.getId());
        if (null == db) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("NNA-1146")
                    .args(role.getId())
                    .throwIt();
        }

        // 禁用检查
        // 超级管理员不能禁用
        if (db.getActive() && null != role.getActive() && !role.getActive()) {
            if (ISysRoleService.SUPER_ADMIN_ROLE.equals(db.getName())) {
                new ExceptionBuilder<>()
                        .code("NNA-1147")
                        .throwIt();
            }
        }

        // 允许修改的项
        // 且有变化才更新
        if ((null != role.getDescription() && !role.getDescription().equals(db.getDescription()))
                || (null != role.getActive() && db.getActive() != role.getActive())
                || (null != role.getName() && !role.getName().equals(db.getName()))) {

            db.setDescription(role.getDescription());
            db.setActive(role.getActive());
            // 超级管理员不允许改角色名称
            if (!ISysRoleService.SUPER_ADMIN_ROLE.equals(db.getName()) && StringUtils.hasText(role.getName())) {
                db.setName(role.getName());
            }

            if (baseMapper.updateById(db) <= 0) {
                new ExceptionBuilder<>().code("NNA-1145").throwIt();
            }
        }

        return db;
    }

}
