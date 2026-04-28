package org.lemon.commons.security.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lemon.commons.core.exceptions.ServiceException;
import org.lemon.commons.core.utils.MapstructUtils;
import org.lemon.commons.redis.utils.RedisUtils;
import org.lemon.commons.security.data.LoginUserInfo;
import org.lemon.commons.security.data.vo.OnlineUserVO;
import org.lemon.commons.security.service.OnlineSessionAdmin;
import org.lemon.commons.security.service.SessionRegistry;
import org.lemon.commons.security.utils.SecurityContextHelper;
import org.redisson.api.RSet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.lemon.commons.core.constant.GlobalConstants.*;
import static org.lemon.commons.security.constant.AuthenticationConstant.REFRESH_TOKEN_EXPIRE_MINUTES;

/**
 * 默认实现：基于 Redisson {@link RSet} 维护 {@code userId → accessToken} 反向索引。
 * <p>同时实现写边 {@link SessionRegistry} 与读边 {@link OnlineSessionAdmin}，
 * 通过 Spring 按接口类型注入分别提供给登录链路 / 管理后台，避免 ISP 违反。</p>
 */
@Slf4j
public class OnlineUserServiceImpl implements SessionRegistry, OnlineSessionAdmin {

    @Override
    public void register(LoginUserInfo loginUserInfo) {
        RSet<String> set = RedisUtils.getClient().getSet(ONLINE_USER + loginUserInfo.getUserId());
        set.add(loginUserInfo.getAccessToken());
        // 集合 TTL 跟随 refresh token，登录/续期时一起被刷新；超过 TTL 后一切自然消亡
        set.expire(Duration.ofMinutes(REFRESH_TOKEN_EXPIRE_MINUTES));
    }

    @Override
    public void unregister(LoginUserInfo loginUserInfo) {
        if (loginUserInfo == null) {
            return;
        }
        if (loginUserInfo.getAccessToken() != null) {
            RedisUtils.deleteObject(ACCESS_TOKEN + loginUserInfo.getAccessToken());
        }
        if (loginUserInfo.getRefreshToken() != null) {
            RedisUtils.deleteObject(REFRESH_TOKEN + loginUserInfo.getRefreshToken());
        }
        if (loginUserInfo.getUserId() != null && loginUserInfo.getAccessToken() != null) {
            RSet<String> set = RedisUtils.getClient().getSet(ONLINE_USER + loginUserInfo.getUserId());
            set.remove(loginUserInfo.getAccessToken());
        }
    }

    @Override
    public boolean kickByToken(String accessToken) {
        // 护栏：禁止踢掉当前请求自身的 token（同账号其他设备的 token 仍允许踢）
        String currentToken = SecurityContextHelper.getTokenValue();
        if (accessToken != null && accessToken.equals(currentToken)) {
            throw new ServiceException("不能踢出当前登录会话");
        }

        LoginUserInfo info = RedisUtils.getCacheObject(ACCESS_TOKEN + accessToken);
        if (info == null) {
            // token 已过期或不存在，仅兜底删一下 key（一般不需要）
            RedisUtils.deleteObject(ACCESS_TOKEN + accessToken);
            return false;
        }
        unregister(info);
        log.info("管理员踢出在线会话: userId={}, token={}", info.getUserId(), accessToken);
        return true;
    }

    @Override
    public int kickByUserId(Long userId) {
        // 护栏：禁止踢出自己（一锅端会包含当前会话）
        Long currentUserId = SecurityContextHelper.getLoginUserId();
        if (userId != null && userId.equals(currentUserId)) {
            throw new ServiceException("不能踢出当前登录用户");
        }

        RSet<String> set = RedisUtils.getClient().getSet(ONLINE_USER + userId);
        Set<String> tokens = set.readAll();
        int count = 0;
        for (String token : tokens) {
            if (kickByToken(token)) {
                count++;
            }
        }
        // 兜底：清空残留集合
        set.delete();
        return count;
    }

    @Override
    public List<OnlineUserVO> listOnline() {
        Collection<String> keys = RedisUtils.keys(ONLINE_USER + "*");
        List<OnlineUserVO> result = new ArrayList<>();
        for (String key : keys) {
            RSet<String> set = RedisUtils.getClient().getSet(key);
            for (String token : set.readAll()) {
                LoginUserInfo info = RedisUtils.getCacheObject(ACCESS_TOKEN + token);
                if (info == null) {
                    // 懒清理：access token 已过期但残留在集合中
                    set.remove(token);
                    continue;
                }
                result.add(MapstructUtils.convert(info, OnlineUserVO.class));
            }
        }
        return result;
    }
}
