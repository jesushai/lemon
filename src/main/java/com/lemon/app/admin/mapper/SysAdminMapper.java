package com.lemon.app.admin.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lemon.app.admin.entity.SysAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统人员 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-23
 */
public interface SysAdminMapper extends BaseMapper<SysAdmin> {

    IPage<SysAdmin> selectPageByExample(IPage<SysAdmin> page, @Param("example") SysAdmin example);

    /**
     * 根据登录名和租户查询管理员
     * 禁用自动租户解析器
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysAdmin> selectListByUsernameAndTenant(@Param("username") String username, @Param("tenant") Long tenant);

    /**
     * 查询所有租户中的指定登录名的管理员
     * 不限制租户需要加@InterceptorIgnore(tenantLine = "true")
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysAdmin> selectListByUsername(String username);

    /**
     * 手动插入数据，只用于初始化的场景
     * 忽略当前操作员的租户，直接强插，仅用于系统自动运行时写入默认的管理员
     */
    int insertAdmin(SysAdmin admin);
}
