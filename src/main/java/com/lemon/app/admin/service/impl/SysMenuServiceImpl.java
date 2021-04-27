package com.lemon.app.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.app.admin.dto.MenuDto;
import com.lemon.app.admin.entity.SysMenu;
import com.lemon.app.admin.mapper.SysMenuMapper;
import com.lemon.app.admin.mapper.SysRolePermissionMapper;
import com.lemon.app.admin.service.ISysMenuService;
import com.lemon.app.core.AppComponentHolder;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.model.PermissionTreeNode;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.util.LoggerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final PermissionService permissionService;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        // 从数据库中查询
        SysMenu db = baseMapper.selectById(id);
        if (null == db) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("NNAM-1001")
                    .args(id)
                    .throwIt();
        }

        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuDto updateMenu(MenuDto menu) {
        if (null == menu || null == menu.getId()) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("ARG-NOT-VALID")
                    .args("id")
                    .throwIt();
        }

        // 从数据库中查询
        SysMenu db = baseMapper.selectById(menu.getId());
        if (null == db) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("NNAM-1001")
                    .args(menu.getId())
                    .throwIt();
        }

        // 仅允许修改显示文本、排序、图标、是否可用、分割线、描述、action地址
        if (StringUtils.isNotEmpty(menu.getDisplay())) {
            db.setDisplay(menu.getDisplay());
        }
        if (null != menu.getRank()) {
            db.setRank(menu.getRank());
        }
        if (StringUtils.isNotEmpty(menu.getIconUrl())) {
            db.setIconUrl(menu.getIconUrl());
        }
        if (null != menu.getActive()) {
            db.setActive(menu.getActive());
        }
        if (null != menu.getSeparation()) {
            db.setSeparation(menu.getSeparation());
        }
        if (null != menu.getDescription()) {
            db.setDescription(menu.getDescription());
        }
        if (db.getAction() && StringUtils.isNotEmpty(menu.getActionUri())) {
            db.setActionUri(menu.getActionUri());
        }

        if (baseMapper.updateById(db) <= 0) {
            new ExceptionBuilder<>().code("NNAM-1002").throwIt();
        }

        BeanUtils.copyProperties(db, menu);
        return menu;
    }

    @Override
    public List<MenuDto> selectTenantMenu() {
        // 获取所有的菜单树
        List<SysMenu> allMenu = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getSystem, AppComponentHolder.getSystemName())
                .eq(SysMenu::getActive, true));

        final List<MenuDto> tree = new ArrayList<>();
        if (CollectionUtils.isEmpty(allMenu)) {
            return tree;
        }

        // 拼装成树结构
        allMenu.stream().filter(m -> m.getLevel() == 1 && m.getPartOf() == null)
                .sorted(Comparator.comparingInt(SysMenu::getRank))
                .forEach(m -> {
                    MenuDto menu = new MenuDto();
                    BeanUtils.copyProperties(m, menu);
                    // 国际化转换
                    String menuDisplay = MessageSourceHandler.getMessageOrNonLocale(menu.getCode(), menu.getDisplay());
                    if (null != menuDisplay) {
                        menu.setDisplay(menuDisplay);
                    }
                    // 加入树结构
                    tree.add(menu);
                    // 下级菜单
                    childrenMenu(allMenu, menu);
                });

        // 获取当前租户的所有权限
        List<String> permissions = rolePermissionMapper.selectAllPermission();

        // 筛除没有权限的菜单树
        for (int i = tree.size() - 1; i >= 0; i--) {
            MenuDto root = tree.get(i);
            filterPermission(root, permissions);
            if (CollectionUtils.isEmpty(root.getChildren())) {
                tree.remove(root);
            }
        }

        return tree;
    }

    /**
     * 过滤没有权限的菜单（租户内）
     */
    private void filterPermission(MenuDto parent, List<String> permissions) {
        if (parent.getChildren() != null && parent.getChildren().size() > 0) {
            for (int i = parent.getChildren().size() - 1; i >= 0; i--) {
                MenuDto child = parent.getChildren().get(i);
                if (child.getAction() && !permissions.contains(child.getCode())) {
                    // 按钮没有权限
                    parent.getChildren().remove(child);
                } else if (!child.getAction()) {
                    // 不是按钮是菜单，继续向下一级过滤
                    filterPermission(child, permissions);
                    // 如果下级菜单都被过滤掉了说明没有权限
                    if (CollectionUtils.isEmpty(child.getChildren())) {
                        parent.getChildren().remove(child);
                    }
                }
            }
        }
    }

    /**
     * 拼装下级菜单
     */
    private void childrenMenu(List<SysMenu> allMenu, MenuDto parent) {
        // 过滤下级子菜单
        List<SysMenu> childrenEntities = allMenu.stream().filter(m -> parent.getId().equals(m.getPartOf()))
                .sorted(Comparator.comparingInt(SysMenu::getRank))
                .collect(Collectors.toList());

        if (childrenEntities.size() > 0) {
            // 加入到父节点
            parent.setChildren(new ArrayList<>());

            for (SysMenu childrenEntity : childrenEntities) {
                MenuDto menu = new MenuDto();
                BeanUtils.copyProperties(childrenEntity, menu);
                // 国际化转换
                String menuDisplay = MessageSourceHandler.getMessageOrNonLocale(menu.getCode(), menu.getDisplay());
                if (null != menuDisplay) {
                    menu.setDisplay(menuDisplay);
                }
                parent.getChildren().add(menu);
                // 递归下级菜单
                childrenMenu(allMenu, menu);
            }
        }
    }

    @Override
    @Async(BeanNameConstants.CORE_ASYNC_POOL)
    @Transactional(rollbackFor = Exception.class)
    public void initMenuAsync(String system, String module, String basePackage) {
        List<PermissionTreeNode> permissions = permissionService.getPermissionTree(basePackage);
        if (null != permissions) {
            for (PermissionTreeNode permission : permissions) {
                tryAddMenu(system, module, permission, 1, null);
            }
        }
    }

    private void tryAddMenu(String system, String module, PermissionTreeNode node, int level, SysMenu parent) {
        SysMenu menu;
        // 查询是否已经存在这个菜单
        menu = baseMapper.selectMenuBySystemAndModuleAndCode(system, module, node.getId());
        if (null == menu) {
            // 不存在则新建
            menu = new SysMenu();
            menu.setSystem(system);
            menu.setModule(module);
            menu.setCode(node.getId());
            menu.setDisplay(node.getLabel());
            menu.setRank(1000); // 先赋默认值，要求用户手动修改
            menu.setIconUrl(StringUtils.EMPTY);
            menu.setLevel(level);
            menu.setPartOf(null == parent ? null : parent.getId());
            menu.setActive(true);
            menu.setDeleted(false);
            menu.setSeparation(false); // 默认不分组
            menu.setDescription(node.getLabel());
            menu.setAction(CollectionUtils.isEmpty(node.getChildren()));
            menu.setActionUri(null == node.getApi() ? StringUtils.EMPTY : node.getApi());

            int n = baseMapper.insert(menu);
            if (n <= 0) {
                new ExceptionBuilder<>()
                        .code("DB-SAVE-MENU-ERROR")
                        .throwIt();
            }
            LoggerUtils.info(log, "Save menu: [{}:{}] - {}({})", system, module, menu.getCode(), menu.getDisplay());
        }

        if (!CollectionUtils.isEmpty(node.getChildren())) {
            // 下级菜单
            for (int i = 0; i < node.getChildren().size(); i++) {
                tryAddMenu(system, module, node.getChildren().get(i), level + 1, menu);
            }
        }
    }

}
