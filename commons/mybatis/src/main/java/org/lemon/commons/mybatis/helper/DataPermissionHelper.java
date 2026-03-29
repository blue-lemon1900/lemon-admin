package org.lemon.commons.mybatis.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lemon.commons.core.utils.reflect.ReflectUtils;
import org.lemon.commons.mybatis.annotation.DataPermission;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * 数据权限助手
 *
 * @author Lion Li
 * @version 3.5.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class DataPermissionHelper {

    private static final String DATA_PERMISSION_KEY = "data:permission";

    private static final ThreadLocal<Stack<Integer>> REENTRANT_IGNORE = ThreadLocal.withInitial(Stack::new);

    private static final ThreadLocal<DataPermission> PERMISSION_CACHE = new ThreadLocal<>();

    /**
     * 获取当前执行mapper权限注解
     *
     * @return 返回当前执行mapper权限注解
     */
    public static DataPermission getPermission() {
        return PERMISSION_CACHE.get();
    }

    /**
     * 设置当前执行mapper权限注解
     *
     * @param dataPermission 数据权限注解
     */
    public static void setPermission(DataPermission dataPermission) {
        PERMISSION_CACHE.set(dataPermission);
    }

    /**
     * 删除当前执行mapper权限注解
     */
    public static void removePermission() {
        PERMISSION_CACHE.remove();
    }

    /**
     * 从上下文中获取指定键的变量值，并将其转换为指定的类型
     *
     * @param key 变量的键
     * @param <T> 变量值的类型
     * @return 指定键的变量值，如果不存在则返回 null
     */
    public static <T> T getVariable(String key) {
        Map<String, Object> context = getContext();
        return (T) context.get(key);
    }

    /**
     * 向上下文中设置指定键的变量值
     *
     * @param key   要设置的变量的键
     * @param value 要设置的变量值
     */
    public static void setVariable(String key, Object value) {
        Map<String, Object> context = getContext();
        context.put(key, value);
    }

    /**
     * 获取数据权限上下文
     *
     * @return 存储在SaStorage中的Map对象，用于存储数据权限相关的上下文信息
     * @throws NullPointerException 如果数据权限上下文类型异常，则抛出NullPointerException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getContext() {
        // 从 Spring 的 RequestContextHolder 获取当前请求的 RequestAttributes（绑定在本线程上）
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("没有请求上下文绑定到线程");
        }

        // 尝试从 RequestAttributes 的 REQUEST 级别作用域中获取名为 DATA_PERMISSION_KEY 的属性
        Object attr = attrs.getAttribute(DATA_PERMISSION_KEY, RequestAttributes.SCOPE_REQUEST);
        if (!(attr instanceof Map)) {
            // 如果属性不存在或类型不对，就新建一个 HashMap 来做容器
            attr = new HashMap<String, Object>();
            // 将新建的 Map 存回 RequestAttributes，生命周期与当前请求一致
            attrs.setAttribute(DATA_PERMISSION_KEY, attr, RequestAttributes.SCOPE_REQUEST);
        }

        return (Map<String, Object>) attr;
    }

    /**
     * 开启忽略数据权限(开启后需手动调用 {@link #disableIgnore()} 关闭)
     */
    private static void enableIgnore() {
        IgnoreStrategy ignoreStrategy = getIgnoreStrategy();
        if (ObjectUtil.isNull(ignoreStrategy)) {
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().dataPermission(true).build());
        } else {
            ignoreStrategy.setDataPermission(true);
        }
        Stack<Integer> reentrantStack = REENTRANT_IGNORE.get();
        reentrantStack.push(reentrantStack.size() + 1);
    }

    /**
     * 关闭忽略数据权限
     */
    private static void disableIgnore() {
        IgnoreStrategy ignoreStrategy = getIgnoreStrategy();
        if (ObjectUtil.isNotNull(ignoreStrategy)) {
            boolean noOtherIgnoreStrategy = !Boolean.TRUE.equals(ignoreStrategy.getDynamicTableName())
                    && !Boolean.TRUE.equals(ignoreStrategy.getBlockAttack())
                    && !Boolean.TRUE.equals(ignoreStrategy.getIllegalSql())
                    && !Boolean.TRUE.equals(ignoreStrategy.getTenantLine())
                    && CollectionUtil.isEmpty(ignoreStrategy.getOthers());
            Stack<Integer> reentrantStack = REENTRANT_IGNORE.get();
            boolean empty = reentrantStack.isEmpty() || reentrantStack.pop() == 1;
            if (noOtherIgnoreStrategy && empty) {
                InterceptorIgnoreHelper.clearIgnoreStrategy();
            } else if (empty) {
                ignoreStrategy.setDataPermission(false);
            }

        }
    }

    /**
     * 在忽略数据权限中执行
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Runnable handle) {
        enableIgnore();
        try {
            handle.run();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略数据权限中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Supplier<T> handle) {
        enableIgnore();
        try {
            return handle.get();
        } finally {
            disableIgnore();
        }
    }

    private static IgnoreStrategy getIgnoreStrategy() {
        Object ignoreStrategyLocal = ReflectUtils.getStaticFieldValue(ReflectUtils.getField(InterceptorIgnoreHelper.class, "IGNORE_STRATEGY_LOCAL"));
        if (ignoreStrategyLocal instanceof ThreadLocal<?> IGNORE_STRATEGY_LOCAL) {
            if (IGNORE_STRATEGY_LOCAL.get() instanceof IgnoreStrategy ignoreStrategy) {
                return ignoreStrategy;
            }
        }
        return null;
    }
}
