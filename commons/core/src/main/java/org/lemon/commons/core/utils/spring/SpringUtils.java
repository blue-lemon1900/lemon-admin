package org.lemon.commons.core.utils.spring;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.thread.Threading;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Spring 工具类
 *
 * @author Lemon
 */
@Component
public class SpringUtils extends SpringContextHolder {

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    /**
     * @return Class 注册对象的类型
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }

    /**
     * 获取aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) getBean(invoker.getClass());
    }

    /**
     * 获取spring上下文
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }

    /**
     * 判断当前应用是否在使用“虚拟线程”
     * 这个方法会检查两个条件：
     * 1.当前运行的 JVM 是 Java 21 或以上；
     * 2.配置文件中开启了 spring.threads.virtual.enabled=true。
     *
     * @return 如果上述两者都满足，就返回 true，否则返回 false。
     */
    public static boolean isVirtual() {
        return Threading.VIRTUAL.isActive(getBean(Environment.class));
    }
}
