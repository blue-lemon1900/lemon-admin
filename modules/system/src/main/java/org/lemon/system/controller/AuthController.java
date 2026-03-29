package org.lemon.system.controller;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.core.utils.MapstructUtils;
import org.lemon.commons.core.utils.StreamUtils;
import org.lemon.commons.ratelimiter.annotation.RateLimiter;
import org.lemon.commons.ratelimiter.enums.LimitType;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.vo.AuthLoginRespVO;
import org.lemon.commons.security.service.UsernameService;
import org.lemon.commons.security.utils.SecurityUtil;
import org.lemon.commons.tenant.helper.TenantHelper;
import org.lemon.system.domain.bo.RefreshTokenBo;
import org.lemon.system.domain.bo.SysTenantBo;
import org.lemon.system.domain.vo.LoginTenantVo;
import org.lemon.system.domain.vo.SysTenantVo;
import org.lemon.system.domain.vo.TenantListVo;
import org.lemon.system.service.ISysTenantService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 认证管理（刷新令牌、退出登录）
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsernameService usernameService;

    private final ISysTenantService tenantService;

    /**
     * 刷新令牌
     * <p>使用 refresh token 换取一组新的访问令牌和刷新令牌，无需携带有效 access token</p>
     *
     * @param bo 包含刷新令牌的请求体
     * @return 含新令牌的用户信息
     */
    @PermitAll
    @PostMapping("/refresh")
    public R<AuthLoginRespVO> refreshToken(@RequestBody @Validated RefreshTokenBo bo) {
        LoginUserInfo userInfo = usernameService.updateToken(bo.getRefreshToken());
        return R.success(MapstructUtils.convert(userInfo, AuthLoginRespVO.class));
    }

    /**
     * 退出登录
     * <p>清除当前用户在 Redis 中的 access token 和 refresh token</p>
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        usernameService.logout();
        return R.success();
    }

    /**
     * 登录页面租户下拉框
     *
     * @return 租户列表
     */
    @PermitAll
    @RateLimiter(time = 60, count = 20, limitType = LimitType.IP)
    @GetMapping("/tenant/list")
    public R<LoginTenantVo> tenantList(HttpServletRequest request) throws Exception {
        // 返回对象
        LoginTenantVo result = new LoginTenantVo();
        boolean enable = TenantHelper.isEnable();
        result.setTenantEnabled(enable);
        // 如果未开启租户这直接返回
        if (!enable) {
            return R.success(result);
        }

        List<SysTenantVo> tenantList = tenantService.queryList(new SysTenantBo());
        List<TenantListVo> voList = MapstructUtils.convert(tenantList, TenantListVo.class);
        // 如果只超管返回所有租户
        if (SecurityUtil.isSuperAdmin()) {
            result.setVoList(voList);
            return R.success(result);
        }

        // 获取域名
        String host;
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) {
            // 这里从referer中取值是为了本地使用hosts添加虚拟域名，方便本地环境调试
            host = referer.split("//")[1].split("/")[0];
        } else {
            host = URI.create(request.getRequestURL().toString()).toURL().getHost();
        }
        // 根据域名进行筛选
        List<TenantListVo> list = StreamUtils.filter(voList, vo -> Strings.CS.equals(vo.getDomain(), host));
        result.setVoList(CollUtil.isNotEmpty(list) ? list : voList);
        return R.success(result);
    }
}
