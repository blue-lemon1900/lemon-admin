package org.lemon.system.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.mybatis.core.page.PageQuery;
import org.lemon.commons.mybatis.core.page.TableDataInfo;
import org.lemon.commons.security.annotation.RequireSuperAdminAndPerm;
import org.lemon.commons.security.data.vo.OnlineUserVO;
import org.lemon.commons.security.service.OnlineSessionAdmin;
import org.lemon.system.domain.bo.OnlineUserQueryBo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户管理（仅超级管理员可见）。
 * <p>支持「分页列出在线会话（可按用户名搜索）」「按 token 踢单端」「按 userId 踢全部端」。</p>
 */
@RestController
@RequestMapping("/system/online")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineSessionAdmin onlineSessionAdmin;

    /**
     * 分页查询在线会话。
     * <p>
     * 因为数据源是 Redis（无服务端过滤/分页能力），实现上采取「全量读 + 内存过滤 + 假分页」：
     * service 层捞出当前所有在线会话，controller 层按 {@code username} 模糊过滤后切片。
     * 在线会话数受并发用户数约束，万级以内毫秒级返回；不必为搜索另建 Redis 反向索引。
     * </p>
     */
    @GetMapping("/list")
    @RequireSuperAdminAndPerm("monitor:online:query")
    public TableDataInfo<OnlineUserVO> list(OnlineUserQueryBo bo, PageQuery pageQuery) {
        List<OnlineUserVO> all = onlineSessionAdmin.listOnline();
        List<OnlineUserVO> filtered = filterByUsername(all, bo.getUsername());
        return TableDataInfo.build(filtered, pageQuery.build());
    }

    /**
     * 踢出指定 token 对应的单个端
     */
    @DeleteMapping("/token/{token}")
    @RequireSuperAdminAndPerm("monitor:online:batchLogout")
    public R<Boolean> kickByToken(@PathVariable String token) {
        return R.success(onlineSessionAdmin.kickByToken(token));
    }

    /**
     * 踢出指定用户全部端
     *
     * @return 实际下线的会话数
     */
    @DeleteMapping("/user/{userId}")
    @RequireSuperAdminAndPerm("monitor:online:forceLogout")
    public R<Integer> kickByUserId(@PathVariable Long userId) {
        return R.success(onlineSessionAdmin.kickByUserId(userId));
    }

    private List<OnlineUserVO> filterByUsername(List<OnlineUserVO> all, String username) {
        if (StringUtils.isBlank(username)) {
            return all;
        }
        String keyword = username.toLowerCase();
        return all.stream()
                .filter(vo -> vo.getUsername() != null && vo.getUsername().toLowerCase().contains(keyword))
                .toList();
    }
}
