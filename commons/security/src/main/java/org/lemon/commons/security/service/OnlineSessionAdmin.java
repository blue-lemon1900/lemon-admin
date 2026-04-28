package org.lemon.commons.security.service;

import org.lemon.commons.security.data.vo.OnlineUserVO;

import java.util.List;

/**
 * 在线会话「读边 + 管理操作」。
 * <p>
 * 仅供管理后台（超级管理员）使用：列出在线会话、按 token 踢单端、按 userId 一键踢全部端。
 *
 * @see SessionRegistry 写边
 */
public interface OnlineSessionAdmin {

    /**
     * 按 access token 踢人。
     * <p>会读取 {@code access_token:{token}} 拿到 {@code LoginUserInfo}，再调用 {@code SessionRegistry.unregister}。
     * token 已过期时仅兜底删除 key。</p>
     * <p>护栏：当 {@code accessToken} 等于当前请求自身的 token 时抛 {@code ServiceException}，
     * 防止管理员把自己当前的会话踢掉（同账号的其他设备会话仍允许踢，用于"远程登出旧设备"）。</p>
     *
     * @return 是否真的下线了一个仍在线的会话
     */
    boolean kickByToken(String accessToken);

    /**
     * 按 userId 踢出该用户全部端。
     * <p>护栏：当 {@code userId} 等于当前登录用户时抛 {@code ServiceException}，
     * 防止管理员把自己一锅端。</p>
     *
     * @return 实际下线的会话数
     */
    int kickByUserId(Long userId);

    /**
     * 列出当前所有在线会话（每端一行）。
     * <p>会顺手懒清理已过期但残留在集合里的 token。</p>
     */
    List<OnlineUserVO> listOnline();
}
