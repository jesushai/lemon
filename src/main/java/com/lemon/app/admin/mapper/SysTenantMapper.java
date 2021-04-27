package com.lemon.app.admin.mapper;

import com.lemon.app.admin.entity.SysTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 租户 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-24
 */
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    void insertTenant(SysTenant tenant);

    List<SysTenant> getAllTenant();

    List<Long> getAllTenantId();

}
