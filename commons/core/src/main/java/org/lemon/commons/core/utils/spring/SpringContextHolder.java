package org.lemon.commons.core.utils.spring;

import lombok.Getter;
import org.lemon.commons.core.exceptions.UtilException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * "@PostConstruct"注解标记的类中，由于ApplicationContext还未加载，导致空指针<br>
     * 因此实现BeanFactoryPostProcessor注入ConfigurableListableBeanFactory实现bean的操作
     */
    private static ConfigurableListableBeanFactory beanFactory;

    /**
     * Spring应用上下文环境
     * 获取
     *
     * @return {@link ApplicationContext}
     */
    @Getter
    private static ApplicationContext applicationContext;

    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringContextHolder.beanFactory = beanFactory;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取{@link ListableBeanFactory}，可能为{@link ConfigurableListableBeanFactory} 或 {@link ApplicationContextAware}
     *
     * @return {@link ListableBeanFactory}
     */
    public static ListableBeanFactory getBeanFactory() {
        final ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new UtilException("没有注入ConfigurableListableBeanFactory或ApplicationContext，可能不在Spring环境中？");
        }
        return factory;
    }

    /**
     * 通过class获取Bean
     *
     * @param <T>   Bean类型
     * @param clazz Bean类
     * @return Bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    /**
     * 通过name获取 Bean
     *
     * @param <T>  Bean类型
     * @param name Bean名称
     * @return Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param <T>   bean类型
     * @param name  Bean名称
     * @param clazz bean类型
     * @return Bean对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param <T>          属性值类型
     * @param key          配置项key
     * @param targetType   配置项类型
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key          配置项key
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static String getProperty(String key, String defaultValue) {
        return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key 配置项key
     * @return 属性值
     */
    public static String getProperty(String key) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }
}
