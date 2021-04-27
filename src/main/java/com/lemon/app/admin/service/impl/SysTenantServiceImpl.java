package com.lemon.app.admin.service.impl;

import com.lemon.app.admin.entity.SysTenant;
import com.lemon.app.admin.mapper.SysTenantMapper;
import com.lemon.app.admin.service.ISysTenantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.framework.auth.TenantService;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 租户 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2021-04-24
 */
@Service(BeanNameConstants.TENANT_SERVICE)
@Transactional(readOnly = true)
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService, TenantService {

    private static final Map<Long, TenantCacheData> cache = new ConcurrentHashMap<>();

    @Override
    public List<Long> getAllTenantId() {
        List<SysTenant> tenants = baseMapper.getAllTenant();
        List<Long> result = new ArrayList<>();

        cache.clear();
        tenants.forEach(tenant -> {
            cache.put(tenant.getId(), new TenantCacheData(tenant));
            result.add(tenant.getId());
        });

        return result;
    }

    @Override
    public String[] getModules(Long tenantId) {
        TenantCacheData t = tryGetTenant(tenantId);
        return t.getModules();
    }

    @Override
    public String getTenantName(Long tenantId) {
        TenantCacheData t = tryGetTenant(tenantId);
        return t.getName();
    }

    @PreDestroy
    private void destroy() {
        cache.clear();
    }

    private TenantCacheData tryGetTenant(Long tenantId) {
        TenantCacheData data = cache.get(tenantId);
        if (null == data || System.currentTimeMillis() - data.timestamp > 3600000L) {
            // 没有缓存或者缓存已过期，1小时缓存
            SysTenant tenant = baseMapper.selectById(tenantId);
            if (null == tenant) {
                new ExceptionBuilder<>(NotFoundException.class)
                        .code("NNS-1001")
                        .throwIt();
            } else {
                data = new TenantCacheData(tenant);
                cache.put(tenantId, data);
            }
        }
        return data;
    }

    @Value
    private static class TenantCacheData {
        long id;
        String[] modules;
        String name;
        long timestamp;

        TenantCacheData(SysTenant tenant) {
            this.id = tenant.getId();
            this.modules = tenant.getModules();
            this.name = tenant.getName();
            this.timestamp = System.currentTimeMillis();
        }
    }
}
