package com.lemon.app.admin.mapper;

import com.lemon.app.admin.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.framework.auth.model.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取指定租户的系统授权
     */
    Set<Permission> selectPermissionBySystem(@Param("system") String system, @Param("tenant") Long tenant);

    SysMenu selectMenuBySystemAndModuleAndCode(@Param("system") String system, @Param("module") String module, @Param("code") String code);

}
