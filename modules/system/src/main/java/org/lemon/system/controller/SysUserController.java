package org.lemon.system.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.StreamUtils;
import org.lemon.commons.core.utils.StringUtils;
import org.lemon.commons.encrypt.annotation.ApiEncrypt;
import org.lemon.commons.excel.core.ExcelResult;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.mybatis.helper.DataPermissionHelper;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.model.RoleModel;
import org.lemon.commons.security.utils.SecurityUtil;
import org.lemon.commons.tenant.helper.TenantHelper;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysDeptBo;
import org.lemon.system.domain.bo.SysPostBo;
import org.lemon.system.domain.bo.SysRoleBo;
import org.lemon.system.domain.bo.SysUserBo;
import org.lemon.system.domain.vo.*;
import org.lemon.system.listener.SysUserImportListener;
import org.lemon.system.service.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysTenantService tenantService;
    private final ISysConfigService configService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo<SysUserVo> list(SysUserBo user, PageQuery pageQuery) {
        return userService.selectPageUserList(user, pageQuery);
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @PostMapping("/export")
    public void export(SysUserBo user, HttpServletResponse response) {
        List<SysUserExportVo> list = userService.selectUserExportList(user);
        ExcelUtil.exportExcel(list, "用户数据", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        SysUserImportListener sysUserImportListener = new SysUserImportListener(updateSupport, userService, configService, passwordEncoder);
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, sysUserImportListener);
        return R.success(result.getAnalysis());
    }

    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R<UserInfoVo> getInfo() {
        LoginUserInfo loginUser = SecurityUtil.getLoginUser();
        if (TenantHelper.isEnable() && SecurityUtil.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }

        SysUserVo user = DataPermissionHelper.ignore(() -> userService.selectUserById(loginUser.getUserId()));
        if (ObjectUtil.isNull(user)) {
            return R.fail("没有权限访问用户数据!");
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getPermissions());
        userInfoVo.setRoles(loginUser.getRoles().stream().map(RoleModel::getRoleKey).collect(Collectors.toSet()));
        return R.success(userInfoVo);
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId 用户ID
     */
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public R<SysUserInfoVo> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        if (ObjectUtil.isNotNull(userId)) {
            userService.checkUserDataScope(userId);
            SysUserVo sysUser = userService.selectUserById(userId);
            userInfoVo.setUser(sysUser);
            userInfoVo.setRoleIds(roleService.selectRoleListByUserId(userId));
            Long deptId = sysUser.getDeptId();
            if (ObjectUtil.isNotNull(deptId)) {
                SysPostBo postBo = new SysPostBo();
                postBo.setDeptId(deptId);
                userInfoVo.setPosts(postService.selectPostList(postBo));
                userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
            }
        }
        SysRoleBo roleBo = new SysRoleBo();
        roleBo.setStatus(SystemConstants.NORMAL);
        List<SysRoleVo> roles = roleService.selectRoleList(roleBo);
        userInfoVo.setRoles(SecurityUtil.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.success(userInfoVo);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUserBo user) {
        deptService.checkDeptDataScope(user.getDeptId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        if (TenantHelper.isEnable()) {
            if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
                return R.fail("当前租户下用户名额不足，请联系管理员");
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @param userIds 角色ID串
     */
    @PreAuthorize("@ss.hasPermission('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtil.contains(userIds, SecurityUtil.getLoginUserId())) {
            return R.fail("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 根据用户ID串批量获取用户基础信息
     *
     * @param userIds 用户ID串
     * @param deptId  部门ID
     */
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/optionselect")
    public R<List<SysUserVo>> optionselect(@RequestParam(required = false) Long[] userIds,
                                           @RequestParam(required = false) Long deptId) {
        return R.success(userService.selectUserByIds(ArrayUtil.isEmpty(userIds) ? null : List.of(userIds), deptId));
    }

    /**
     * 重置密码
     */
    @ApiEncrypt
    @PreAuthorize("@ss.hasPermission('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toAjax(userService.resetUserPwd(user.getUserId(), user.getPassword()));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        return toAjax(userService.updateUserStatus(user.getUserId(), user.getStatus()));
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId 用户ID
     */
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public R<SysUserInfoVo> authRole(@PathVariable Long userId) {
        userService.checkUserDataScope(userId);
        SysUserVo user = userService.selectUserById(userId);
        List<SysRoleVo> roles = roleService.selectRolesAuthByUserId(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);

        userInfoVo.setRoles(SecurityUtil.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.success(userInfoVo);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户Id
     * @param roleIds 角色ID串
     */
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @RepeatSubmit()
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.success();
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.success(deptService.selectDeptTreeList(dept));
    }

    /**
     * 获取部门下的所有用户信息
     */
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @GetMapping("/list/dept/{deptId}")
    public R<List<SysUserVo>> listByDept(@PathVariable @NotNull Long deptId) {
        return R.success(userService.selectUserListByDept(deptId));
    }

}
