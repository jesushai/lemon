package com.lemon.app.configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.lemon.app.constants.EntityConstants;
import com.lemon.app.handler.MyFillMetaObjectHandler;
import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;

/**
 * 名称：Mybatis Plus 配置器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/18
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.lemon.app.*.mapper*")
public class MybatisPlusAutoConfiguration {

    @Resource
    private AuthenticationService authenticationService;

    private static final String excludeTenantTableNames = "sys_coding_category,base_region,";

    /**
     * 3.4新特性
     * 拦截方式改变了
     * <p>
     * 使用多个功能需要注意顺序关系,建议使用如下顺序
     * <p>
     * 多租户,动态表名
     * 分页,乐观锁
     * sql性能规范,防止全表更新与删除
     * 总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 从请求中获得租户
                User user = authenticationService.getPrincipal();
                Expression tenantId;
                if (null == user)
                    tenantId = new LongValue(0L);
                else
                    tenantId = new LongValue(user.getTenant());
                return tenantId;
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                // 是否需要需要过滤某一张表
                return excludeTenantTableNames.contains(tableName + ',');
            }

            // 设置租户的字段，默认是tenant_id
            @Override
            public String getTenantIdColumn() {
                return EntityConstants.DB_TENANT_FIELD;
            }
        }));

        // 动态表名
//        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
//        HashMap<String, TableNameHandler> map = new HashMap<String, TableNameHandler>(2) {{
//            put("user", (sql, tableName) -> {
//                String year = "_2018";
//                int random = new Random().nextInt(10);
//                if (random % 2 == 1) {
//                    year = "_2019";
//                }
//                return tableName + year;
//            });
//        }};
//
//        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
//        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        // 分页插件
        // 必须放在租户插件之后！
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

//    /**
//     * 3.4.0官方说使用租户和分页插件避免缓存出现问题，这个要关闭
//     * 3.4.2缺将此方法设为不推荐使用
//     * 且必须设置 MybatisConfiguration#useDeprecatedExecutor = false？？？不必了？？？
//     */
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> configuration.setUseDeprecatedExecutor(false);
//    }

    /**
     * 自动填充审计信息
     */
    @Bean
    public MyFillMetaObjectHandler myFillMetaObjectHandler() {
        return new MyFillMetaObjectHandler(authenticationService);
    }
}
