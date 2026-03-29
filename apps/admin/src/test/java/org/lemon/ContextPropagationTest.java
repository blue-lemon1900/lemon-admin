package org.lemon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证 ContextPropagatingTaskDecorator 在 @Async 场景下的 SecurityContext 传播行为。
 * 分别测试平台线程（ThreadPoolTaskExecutor）和虚拟线程（SimpleAsyncTaskExecutor）两种 Executor。
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {ContextPropagationTest.AsyncConfig.class, ContextPropagationTest.AsyncProbeService.class}
)
class ContextPropagationTest {

    @Autowired
    AsyncProbeService probeService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // -----------------------------------------------------------------------
    // 平台线程测试
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("平台线程：SecurityContext 应传播至 @Async 子线程")
    void platformThread_shouldPropagateSecurityContext() throws Exception {
        setAuthentication("alice", "ROLE_USER");

        String username = probeService.captureUsername().get(5, TimeUnit.SECONDS);

        assertThat(username).isEqualTo("alice");
    }

    @Test
    @DisplayName("平台线程：调用方无 SecurityContext 时，子线程也不应有")
    void platformThread_shouldNotLeakContextWhenNoneSet() throws Exception {
        // 不设置任何 SecurityContext

        String username = probeService.captureUsername().get(5, TimeUnit.SECONDS);

        assertThat(username).isNull();
    }

    // -----------------------------------------------------------------------
    // 虚拟线程测试
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("虚拟线程：SecurityContext 应传播至 @Async 虚拟线程")
    void virtualThread_shouldPropagateSecurityContext() throws Exception {
        setAuthentication("bob", "ROLE_ADMIN");

        String username = probeService.captureUsernameOnVirtualThread().get(5, TimeUnit.SECONDS);

        assertThat(username).isEqualTo("bob");
    }

    @Test
    @DisplayName("虚拟线程：调用方无 SecurityContext 时，虚拟线程也不应有")
    void virtualThread_shouldNotLeakContextWhenNoneSet() throws Exception {
        // 不设置任何 SecurityContext

        String username = probeService.captureUsernameOnVirtualThread().get(5, TimeUnit.SECONDS);

        assertThat(username).isNull();
    }

    @Test
    @DisplayName("虚拟线程：确认异步方法确实运行在虚拟线程上")
    void virtualThread_shouldRunOnVirtualThread() throws Exception {
        boolean isVirtual = probeService.isVirtualThread().get(5, TimeUnit.SECONDS);

        assertThat(isVirtual).isTrue();
    }

    // -----------------------------------------------------------------------
    // 测试专用 Spring 配置（不加载完整应用上下文，避免依赖 DB / Redis）
    // -----------------------------------------------------------------------

    @TestConfiguration
    @EnableAsync(proxyTargetClass = true)
    static class AsyncConfig {

        /**
         * 被测对象：ContextPropagatingTaskDecorator
         * 在生产环境中由 SecurityBeanConfig 注册，此处在测试上下文中独立声明。
         */
        @Bean
        public TaskDecorator contextPropagatingTaskDecorator() {
            return new ContextPropagatingTaskDecorator();
        }

        /** 平台线程 Executor，挂载 TaskDecorator */
        @Bean("platformThreadExecutor")
        public AsyncTaskExecutor platformThreadExecutor(TaskDecorator taskDecorator) {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setTaskDecorator(taskDecorator);
            executor.setThreadNamePrefix("test-platform-");
            executor.initialize();
            return executor;
        }

        /** 虚拟线程 Executor，挂载 TaskDecorator（对应生产环境 spring.threads.virtual.enabled=true） */
        @Bean("virtualThreadExecutor")
        public AsyncTaskExecutor virtualThreadExecutor(TaskDecorator taskDecorator) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("test-virtual-");
            executor.setVirtualThreads(true);
            executor.setTaskDecorator(taskDecorator);
            return executor;
        }
    }

    // -----------------------------------------------------------------------
    // 探针 Service：在 @Async 方法内捕获运行时信息
    // -----------------------------------------------------------------------

    @Service
    static class AsyncProbeService {

        /** 在平台线程上捕获当前认证用户名 */
        @Async("platformThreadExecutor")
        public CompletableFuture<String> captureUsername() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return CompletableFuture.completedFuture(auth != null ? auth.getName() : null);
        }

        /** 在虚拟线程上捕获当前认证用户名 */
        @Async("virtualThreadExecutor")
        public CompletableFuture<String> captureUsernameOnVirtualThread() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return CompletableFuture.completedFuture(auth != null ? auth.getName() : null);
        }

        /** 确认当前线程是否为虚拟线程 */
        @Async("virtualThreadExecutor")
        public CompletableFuture<Boolean> isVirtualThread() {
            return CompletableFuture.completedFuture(Thread.currentThread().isVirtual());
        }
    }

    // -----------------------------------------------------------------------
    // 工具方法
    // -----------------------------------------------------------------------

    private void setAuthentication(String username, String role) {
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                username, null, List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
