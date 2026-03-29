package org.lemon.system.controller;

import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.security.utils.SecurityUtil;
import org.lemon.commons.web.core.BaseController;
import org.lemon.system.domain.vo.SysSocialVo;
import org.lemon.system.service.ISysSocialService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 社会化关系
 *
 * @author thiszhc
 * @date 2023-06-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/social")
public class SysSocialController extends BaseController {

    private final ISysSocialService socialUserService;

    /**
     * 查询社会化关系列表
     */
    @GetMapping("/list")
    public R<List<SysSocialVo>> list() {
        return R.success(socialUserService.queryListByUserId(SecurityUtil.getLoginUserId()));
    }

}
