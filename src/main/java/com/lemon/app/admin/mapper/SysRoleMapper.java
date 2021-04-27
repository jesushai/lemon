package com.lemon.app.admin.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.lemon.app.admin.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统角色表 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 手动插入数据，只用于初始化的场景
     *
     * @param tenantId 租户id
     * @param role     角色实体
     * @return 成功插入的记录
     */
    int insertRole(@Param("tenant") Long tenantId, @Param("role") SysRole role);

    @InterceptorIgnore(tenantLine = "true")
    SysRole selectOneByNameAndTenant(@Param("tenant") Long id, @Param("name") String name);

    List<SysRole> selectListByIdInAndActiveIsTrue(Long[] ids);

    Set<String> selectRoleNameByIdIn(Set<Long> ids);

    List<SysRole> selectListByExample(SysRole role);

}
