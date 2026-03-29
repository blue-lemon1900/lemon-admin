package org.lemon.system.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.constant.SystemConstants;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.excel.utils.ExcelUtil;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysDeptBo;
import org.lemon.system.domain.bo.SysPostBo;
import org.lemon.system.domain.vo.SysPostVo;
import org.lemon.system.service.ISysDeptService;
import org.lemon.system.service.ISysPostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 岗位信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    private final ISysPostService postService;
    private final ISysDeptService deptService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermission('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo<SysPostVo> list(SysPostBo post, PageQuery pageQuery) {
        return postService.selectPagePostList(post, pageQuery);
    }

    /**
     * 导出岗位列表
     */
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:post:export')")
    @PostMapping("/export")
    public void export(SysPostBo post, HttpServletResponse response) {
        List<SysPostVo> list = postService.selectPostList(post);
        ExcelUtil.exportExcel(list, "岗位数据", SysPostVo.class, response);
    }

    /**
     * 根据岗位编号获取详细信息
     *
     * @param postId 岗位ID
     */
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    @GetMapping(value = "/{postId}")
    public R<SysPostVo> getInfo(@PathVariable Long postId) {
        return R.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermission('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysPostBo post) {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermission('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysPostBo post) {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        } else if (SystemConstants.DISABLE.equals(post.getStatus())
                && postService.countUserPostById(post.getPostId()) > 0) {
            return R.fail("该岗位下存在已分配用户，不能禁用!");
        }
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     *
     * @param postIds 岗位ID串
     */
    @PreAuthorize("@ss.hasPermission('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        return toAjax(postService.deletePostByIds(Arrays.asList(postIds)));
    }

    /**
     * 获取岗位选择框列表
     *
     * @param postIds 岗位ID串
     * @param deptId  部门id
     */
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    @GetMapping("/optionselect")
    public R<List<SysPostVo>> optionselect(@RequestParam(required = false) Long[] postIds, @RequestParam(required = false) Long deptId) {
        List<SysPostVo> list = new ArrayList<>();
        if (ObjectUtil.isNotNull(deptId)) {
            SysPostBo post = new SysPostBo();
            post.setDeptId(deptId);
            list = postService.selectPostList(post);
        } else if (postIds != null) {
            list = postService.selectPostByIds(List.of(postIds));
        }
        return R.success(list);
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermission('system:post:list')")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.success(deptService.selectDeptTreeList(dept));
    }


}
