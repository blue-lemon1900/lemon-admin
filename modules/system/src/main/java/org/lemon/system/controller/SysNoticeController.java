package org.lemon.system.controller;

import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.service.DictService;
import org.lemon.commons.idempotent.annotation.RepeatSubmit;
import org.lemon.commons.log.annotation.Log;
import org.lemon.commons.log.enums.BusinessType;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.sse.utils.SseMessageUtils;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.bo.SysNoticeBo;
import org.lemon.system.domain.vo.SysNoticeVo;
import org.lemon.system.service.ISysNoticeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告 信息操作处理
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    private final ISysNoticeService noticeService;
    private final DictService dictService;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@ss.hasPermission('system:notice:list')")
    @GetMapping("/list")
    public TableDataInfo<SysNoticeVo> list(SysNoticeBo notice, PageQuery pageQuery) {
        return noticeService.selectPageNoticeList(notice, pageQuery);
    }

    /**
     * 根据通知公告编号获取详细信息
     *
     * @param noticeId 公告ID
     */
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public R<SysNoticeVo> getInfo(@PathVariable Long noticeId) {
        return R.success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermission('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysNoticeBo notice) {
        int rows = noticeService.insertNotice(notice);
        if (rows <= 0) {
            return R.fail();
        }
        String type = dictService.getDictLabel("sys_notice_type", notice.getNoticeType());
        SseMessageUtils.publishAll("[" + type + "] " + notice.getNoticeTitle());
        return R.success();
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermission('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysNoticeBo notice) {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     *
     * @param noticeIds 公告ID串
     */
    @PreAuthorize("@ss.hasPermission('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}
