package org.lemon.system.controller;

import com.baomidou.lock.annotation.Lock4j;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.validate.AddGroup;
import org.lemon.commons.core.validate.EditGroup;
import org.lemon.commons.encrypt.annotation.ApiEncrypt;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.tenant.helper.TenantHelper;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysTenantBo;
import org.lemon.system.domain.vo.SysTenantVo;
import org.lemon.system.service.ISysTenantService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理
 *
 * @author Michelle.Chung
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenant")
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class SysTenantController extends BaseController {

    private final ISysTenantService tenantService;

    /**
     * 查询租户列表
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo<SysTenantVo> list(SysTenantBo bo, PageQuery pageQuery) {
        return tenantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出租户列表
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:export')")
    @Log(title = "租户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantBo bo, HttpServletResponse response) {
        List<SysTenantVo> list = tenantService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户", SysTenantVo.class, response);
    }

    /**
     * 获取租户详细信息
     *
     * @param id 主键
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:query')")
    @GetMapping("/{id}")
    public R<SysTenantVo> getInfo(@NotNull(message = "主键不能为空")
                                  @PathVariable Long id) {
        return R.success(tenantService.queryById(id));
    }

    /**
     * 新增租户
     */
    @ApiEncrypt
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:add')")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @Lock4j
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantBo bo) {
        if (!tenantService.checkCompanyNameUnique(bo)) {
            return R.fail("新增租户'" + bo.getCompanyName() + "'失败，企业名称已存在");
        }
        return toAjax(TenantHelper.ignore(() -> tenantService.insertByBo(bo)));
    }

    /**
     * 修改租户
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantBo bo) {
        tenantService.checkTenantAllowed(bo.getTenantId());
        if (!tenantService.checkCompanyNameUnique(bo)) {
            return R.fail("修改租户'" + bo.getCompanyName() + "'失败，公司名称已存在");
        }
        return toAjax(tenantService.updateByBo(bo));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysTenantBo bo) {
        tenantService.checkTenantAllowed(bo.getTenantId());
        return toAjax(tenantService.updateTenantStatus(bo));
    }

    /**
     * 删除租户
     *
     * @param ids 主键串
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:remove')")
    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(tenantService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 动态切换租户
     *
     * @param tenantId 租户ID
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY)")
    @GetMapping("/dynamic/{tenantId}")
    public R<Void> dynamicTenant(@NotBlank(message = "租户ID不能为空") @PathVariable String tenantId) {
        TenantHelper.setDynamic(tenantId, true);
        return R.success();
    }

    /**
     * 清除动态租户
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY)")
    @GetMapping("/dynamic/clear")
    public R<Void> dynamicClear() {
        TenantHelper.clearDynamic();
        return R.success();
    }


    /**
     * 同步租户套餐
     *
     * @param tenantId  租户id
     * @param packageId 套餐id
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY) and @ss.hasPermission('system:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @Lock4j
    @GetMapping("/syncTenantPackage")
    public R<Void> syncTenantPackage(@NotBlank(message = "租户ID不能为空") String tenantId,
                                     @NotNull(message = "套餐ID不能为空") Long packageId) {
        return toAjax(TenantHelper.ignore(() -> tenantService.syncTenantPackage(tenantId, packageId)));
    }

    /**
     * 同步租户字典
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY)")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @Lock4j
    @GetMapping("/syncTenantDict")
    public R<Void> syncTenantDict() {
        if (!TenantHelper.isEnable()) {
            return R.fail("当前未开启租户模式");
        }
        tenantService.syncTenantDict();
        return R.success("同步租户字典成功");
    }

    /**
     * 同步租户参数配置
     */
    @PreAuthorize("@ss.hasRole(T(org.lemon.commons.core.constant.TenantConstants).SUPER_ADMIN_ROLE_KEY)")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @Lock4j
    @GetMapping("/syncTenantConfig")
    public R<Void> syncTenantConfig() {
        if (!TenantHelper.isEnable()) {
            return R.fail("当前未开启租户模式");
        }
        tenantService.syncTenantConfig();
        return R.success("同步租户参数配置成功");
    }

}
