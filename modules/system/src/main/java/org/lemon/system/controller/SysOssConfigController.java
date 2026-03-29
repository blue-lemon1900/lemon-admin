package org.lemon.system.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.validate.AddGroup;
import org.lemon.commons.core.validate.EditGroup;
import org.lemon.commons.core.validate.QueryGroup;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysOssConfigBo;
import org.lemon.system.domain.vo.SysOssConfigVo;
import org.lemon.system.service.ISysOssConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对象存储配置
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/oss/config")
public class SysOssConfigController extends BaseController {

    private final ISysOssConfigService ossConfigService;

    /**
     * 查询对象存储配置列表
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:list')")
    @GetMapping("/list")
    public TableDataInfo<SysOssConfigVo> list(@Validated(QueryGroup.class) SysOssConfigBo bo, PageQuery pageQuery) {
        return ossConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param ossConfigId OSS配置ID
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:list')")
    @GetMapping("/{ossConfigId}")
    public R<SysOssConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long ossConfigId) {
        return R.success(ossConfigService.queryById(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:add')")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(ossConfigService.insertByBo(bo));
    }

    /**
     * 修改对象存储配置
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:edit')")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(ossConfigService.updateByBo(bo));
    }

    /**
     * 删除对象存储配置
     *
     * @param ossConfigIds OSS配置ID串
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:remove')")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossConfigIds) {
        return toAjax(ossConfigService.deleteWithValidByIds(List.of(ossConfigIds), true));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermission('system:ossConfig:edit')")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysOssConfigBo bo) {
        return toAjax(ossConfigService.updateOssConfigStatus(bo));
    }
}
