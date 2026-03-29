package org.lemon.system.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.validate.AddGroup;
import org.lemon.commons.core.validate.EditGroup;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysTenantPackageBo;
import org.lemon.system.domain.vo.SysTenantPackageVo;
import org.lemon.system.service.ISysTenantPackageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户套餐管理
 *
 * @author Michelle.Chung
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenant/package")
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class SysTenantPackageController extends BaseController {

    private final ISysTenantPackageService tenantPackageService;

    /**
     * 查询租户套餐列表
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:list')")
    @GetMapping("/list")
    public TableDataInfo<SysTenantPackageVo> list(SysTenantPackageBo bo, PageQuery pageQuery) {
        return tenantPackageService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询租户套餐下拉选列表
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:list')")
    @GetMapping("/selectList")
    public R<List<SysTenantPackageVo>> selectList() {
        return R.success(tenantPackageService.selectList());
    }

    /**
     * 导出租户套餐列表
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:export')")
    @Log(title = "租户套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantPackageBo bo, HttpServletResponse response) {
        List<SysTenantPackageVo> list = tenantPackageService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户套餐", SysTenantPackageVo.class, response);
    }

    /**
     * 获取租户套餐详细信息
     *
     * @param packageId 主键
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:query')")
    @GetMapping("/{packageId}")
    public R<SysTenantPackageVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable Long packageId) {
        return R.success(tenantPackageService.queryById(packageId));
    }

    /**
     * 新增租户套餐
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:add')")
    @Log(title = "租户套餐", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantPackageBo bo) {
        if (!tenantPackageService.checkPackageNameUnique(bo)) {
            return R.fail("新增套餐'" + bo.getPackageName() + "'失败，套餐名称已存在");
        }
        return toAjax(tenantPackageService.insertByBo(bo));
    }

    /**
     * 修改租户套餐
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:edit')")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantPackageBo bo) {
        if (!tenantPackageService.checkPackageNameUnique(bo)) {
            return R.fail("修改套餐'" + bo.getPackageName() + "'失败，套餐名称已存在");
        }
        return toAjax(tenantPackageService.updateByBo(bo));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:edit')")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysTenantPackageBo bo) {
        return toAjax(tenantPackageService.updatePackageStatus(bo));
    }

    /**
     * 删除租户套餐
     *
     * @param packageIds 主键串
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenantPackage:remove')")
    @Log(title = "租户套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packageIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] packageIds) {
        return toAjax(tenantPackageService.deleteWithValidByIds(List.of(packageIds), true));
    }
}
