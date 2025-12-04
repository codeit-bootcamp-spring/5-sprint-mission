package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.global.config.MdcTaskDecorator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class MdcTaskDecoratorTest {

    private MdcTaskDecorator decorator;

    @BeforeEach
    void setUp() {
        decorator = new MdcTaskDecorator();
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("MDC 컨텍스트가 비동기 스레드로 전파됨")
    void decorate_PropagatesMdcContext() throws InterruptedException {
        // given
        String requestId = "test-request-id";
        MDC.put("requestId", requestId);

        AtomicReference<String> capturedRequestId = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            capturedRequestId.set(MDC.get("requestId"));
            latch.countDown();
        };

        // when
        Runnable decoratedTask = decorator.decorate(task);
        new Thread(decoratedTask).start();

        // then
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(capturedRequestId.get()).isEqualTo(requestId);
    }

    @Test
    @DisplayName("SecurityContext가 비동기 스레드로 전파됨")
    void decorate_PropagatesSecurityContext() throws InterruptedException {
        // given
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        AtomicReference<String> capturedUsername = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                capturedUsername.set(auth.getName());
            }
            latch.countDown();
        };

        // when
        Runnable decoratedTask = decorator.decorate(task);
        new Thread(decoratedTask).start();

        // then
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(capturedUsername.get()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("작업 완료 후 MDC와 SecurityContext가 정리됨")
    void decorate_ClearsContextAfterExecution() throws InterruptedException {
        // given
        MDC.put("requestId", "test-id");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        AtomicReference<String> mdcAfterTask = new AtomicReference<>();
        AtomicReference<Object> authAfterTask = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            // 작업 수행
        };

        Runnable checkAfterTask = () -> {
            mdcAfterTask.set(MDC.get("requestId"));
            authAfterTask.set(SecurityContextHolder.getContext().getAuthentication());
            latch.countDown();
        };

        // when
        Runnable decoratedTask = decorator.decorate(task);
        new Thread(() -> {
            decoratedTask.run();
            checkAfterTask.run();
        }).start();

        // then
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(mdcAfterTask.get()).isNull();
        assertThat(authAfterTask.get()).isNull();
    }

    @Test
    @DisplayName("MDC가 null인 경우에도 정상 동작")
    void decorate_WorksWithNullMdc() throws InterruptedException {
        // given - MDC is already clear
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> taskExecuted = new AtomicReference<>(false);

        Runnable task = () -> {
            taskExecuted.set(true);
            latch.countDown();
        };

        // when
        Runnable decoratedTask = decorator.decorate(task);
        new Thread(decoratedTask).start();

        // then
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(taskExecuted.get()).isTrue();
    }
}
