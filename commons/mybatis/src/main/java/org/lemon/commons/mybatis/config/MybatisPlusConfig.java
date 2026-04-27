package org.lemon.commons.mybatis.config;

import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.lemon.commons.core.factory.YmlPropertySourceFactory;
import org.lemon.commons.mybatis.aspect.DataPermissionPointcutAdvisor;
import org.lemon.commons.mybatis.config.properties.MybatisPlusExtensionProperties;
import org.lemon.commons.mybatis.handler.InjectionMetaObjectHandler;
import org.lemon.commons.mybatis.handler.MybatisExceptionHandler;
import org.lemon.commons.mybatis.handler.PlusPostInitTableInfoHandler;
import org.lemon.commons.mybatis.interceptor.PlusDataPermissionInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置类，已启用以下插件：
 * <ul>
 *   <li>{@link PaginationInnerInterceptor} 分页插件，自动识别数据库类型
 *       — <a href="https://baomidou.com/pages/97710a/">文档</a></li>
 *   <li>{@link OptimisticLockerInnerInterceptor} 乐观锁插件
 *       — <a href="https://baomidou.com/pages/0d93c0/">文档</a></li>
 *   <li>{@link MetaObjectHandler} 元对象字段填充控制器
 *       — <a href="https://baomidou.com/pages/4c6bcf/">文档</a></li>
 *   <li>ISqlInjector SQL 注入器
 *       — <a href="https://baomidou.com/pages/42ea4a/">文档</a></li>
 *   <li>BlockAttackInnerInterceptor 全表删除/更新拦截
 *       — <a href="https://baomidou.com/pages/f9a237/">文档</a></li>
 *   <li>IllegalSQLInnerInterceptor SQL 性能规范插件（垃圾 SQL 拦截）</li>
 *   <li>{@link IdentifierGenerator} 自定义主键策略（雪花算法绑定网卡）
 *       — <a href="https://baomidou.com/pages/568eb2/">文档</a></li>
 *   <li>{@link TenantLineInnerInterceptor} 多租户插件
 *       — <a href="https://baomidou.com/pages/aef2f2/">文档</a></li>
 *   <li>DynamicTableNameInnerInterceptor 动态表名插件
 *       — <a href="https://baomidou.com/pages/2a45ff/">文档</a></li>
 * </ul>
 *
 * @author Lion Li
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties(MybatisPlusExtensionProperties.class)
@MapperScan("${mybatis-plus.mapper-package}")
@PropertySource(value = "classpath:common-mybatis.yml", factory = YmlPropertySourceFactory.class)
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<TenantLineInnerInterceptor> tenantLineInnerInterceptorProvider) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件 必须放到第一位，有 Bean 则注入，无则跳过
        tenantLineInnerInterceptorProvider.ifAvailable(interceptor::addInnerInterceptor);
        // 数据权限处理
        interceptor.addInnerInterceptor(dataPermissionInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 数据权限拦截器
     */
    public PlusDataPermissionInterceptor dataPermissionInterceptor() {
        return new PlusDataPermissionInterceptor();
    }

    /**
     * 数据权限切面处理器
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataPermissionPointcutAdvisor dataPermissionPointcutAdvisor() {
        return new DataPermissionPointcutAdvisor();
    }

    /**
     * 分页插件，自动识别数据库类型
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 分页合理化
        paginationInnerInterceptor.setOverflow(true);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 元对象字段填充控制器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new InjectionMetaObjectHandler();
    }

    /**
     * 使用网卡信息绑定雪花生成器
     * 防止集群雪花ID重复
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator(NetUtil.getLocalhost());
    }

    /**
     * 异常处理器
     */
    @Bean
    public MybatisExceptionHandler mybatisExceptionHandler() {
        return new MybatisExceptionHandler();
    }

    /**
     * 初始化表对象处理器
     */
    @Bean
    public PostInitTableInfoHandler postInitTableInfoHandler(MybatisPlusExtensionProperties properties) {
        return new PlusPostInitTableInfoHandler(properties);
    }
}
