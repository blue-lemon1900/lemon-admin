package org.lemon.controller;

import jakarta.annotation.security.PermitAll;
import org.lemon.commons.core.domain.result.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author Lemon
 */
@RestController
@RequestMapping
public class IndexController {

    /**
     * 访问首页，提示语
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermission('system:dict:list')")
    public String index() {
        return "测试!!!";
    }

    @PermitAll
    @GetMapping(path = "/save1")
    public R<Void> save(@RequestParam String model) {
        return R.success();
    }
}


