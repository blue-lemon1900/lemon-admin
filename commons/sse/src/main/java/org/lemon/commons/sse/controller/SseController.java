package org.lemon.commons.sse.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.lemon.commons.core.domain.result.R;
import org.lemon.commons.security.utils.SecurityUtil;
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
        if (!SecurityUtil.isLogin()) {
            return null;
        }
        String tokenValue = SecurityUtil.getTokenValue();
        Long userId = SecurityUtil.getLoginUserId();
        return sseEmitterManager.connect(userId, tokenValue);
    }

    /**
     * 关闭 SSE 连接
     */
    @PermitAll
    @GetMapping(value = "${sse.path}/close")
    public R<Void> close() {
        String tokenValue = SecurityUtil.getTokenValue();
        Long userId = SecurityUtil.getLoginUserId();
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
