package org.lemon.commons.sse.controller;

import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.security.annotation.AnonymousAccess;
import org.lemon.commons.security.utils.SecurityContextHelper;
import org.lemon.commons.sse.core.SseEmitterManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 控制器
 *
 * @author Lion Li
 */
@RestController
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SseController implements DisposableBean {

    private final SseEmitterManager sseEmitterManager;

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "${sse.path}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        if (!SecurityContextHelper.isLogin()) {
            return null;
        }
        String tokenValue = SecurityContextHelper.getTokenValue();
        Long userId = SecurityContextHelper.getLoginUserId();
        return sseEmitterManager.connect(userId, tokenValue);
    }

    /**
     * 关闭 SSE 连接
     */
    @AnonymousAccess
    @GetMapping(value = "${sse.path}/close")
    public R<Void> close() {
        String tokenValue = SecurityContextHelper.getTokenValue();
        Long userId = SecurityContextHelper.getLoginUserId();
        sseEmitterManager.disconnect(userId, tokenValue);
        return R.success();
    }

    /**
     * 清理资源。此方法目前不执行任何操作，但避免因未实现而导致错误
     */
    @Override
    public void destroy() {
        // 销毁时不需要做什么 此方法避免无用操作报错
    }
}
