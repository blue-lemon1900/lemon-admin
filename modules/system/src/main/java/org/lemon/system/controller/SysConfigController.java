package org.lemon.system.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysConfigBo;
import org.lemon.system.domain.vo.SysConfigVo;
import org.lemon.system.service.ISysConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {

    private final ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermission('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo<SysConfigVo> list(SysConfigBo config, PageQuery pageQuery) {
        return configService.selectPageConfigList(config, pageQuery);
    }

    /**
     * 导出参数配置列表
     */
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:config:export')")
    @PostMapping("/export")
    public void export(SysConfigBo config, HttpServletResponse response) {
        List<SysConfigVo> list = configService.selectConfigList(config);
        ExcelUtil.exportExcel(list, "参数数据", SysConfigVo.class, response);
    }

    /**
     * 根据参数编号获取详细信息
     *
     * @param configId 参数ID
     */
    @PreAuthorize("@ss.hasPermission('system:config:query')")
    @GetMapping(value = "/{configId}")
    public R<SysConfigVo> getInfo(@PathVariable Long configId) {
        return R.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数Key
     */
    @GetMapping(value = "/configKey/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey) {
        return R.success("操作成功", configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermission('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysConfigBo config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        configService.insertConfig(config);
        return R.success();
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermission('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysConfigBo config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.fail("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        configService.updateConfig(config);
        return R.success();
    }

    /**
     * 根据参数键名修改参数配置
     */
    @PreAuthorize("@ss.hasPermission('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/updateByKey")
    public R<Void> updateByKey(@RequestBody SysConfigBo config) {
        configService.updateConfig(config);
        return R.success();
    }

    /**
     * 删除参数配置
     *
     * @param configIds 参数ID串
     */
    @PreAuthorize("@ss.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(Arrays.asList(configIds));
        return R.success();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermission('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {
        configService.resetConfigCache();
        return R.success();
    }
}
