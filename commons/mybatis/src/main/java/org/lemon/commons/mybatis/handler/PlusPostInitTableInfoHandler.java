package org.lemon.commons.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;
import org.lemon.commons.core.utils.reflect.ReflectUtils;
import org.lemon.commons.mybatis.config.properties.MybatisPlusExtensionProperties;

/**
 * 修改表信息初始化方式
 * 目前用于全局修改是否使用逻辑删除
 *
 * @author Lion Li
 */
public class PlusPostInitTableInfoHandler implements PostInitTableInfoHandler {

    private final MybatisPlusExtensionProperties properties;

    public PlusPostInitTableInfoHandler(MybatisPlusExtensionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void postTableInfo(TableInfo tableInfo, Configuration configuration) {
        // 只有显式关闭时才覆盖;开启状态由 MP 自身判断
        if (Boolean.FALSE.equals(properties.getEnableLogicDelete())) {
            ReflectUtils.setFieldValue(tableInfo, "withLogicDelete", false);
        }
    }

}
