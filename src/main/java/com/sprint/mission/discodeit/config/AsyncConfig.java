package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-task-");

        // 비동기 스레드에서도 MDC + SecurityContext 유지
        executor.setTaskDecorator(
                new CompositeTaskDecorator(List.of(
                        mdcTaskDecorator(), securityContextTaskDecorator())
                ));

        executor.initialize();

        return executor;
    }

    // MDC 복사 및 비동기 스레드에서도 동일한 MDC Context 유지
    public TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            // 요청 스레드 MDC 전체 스냅샷
            Map<String, String> mdcMap = MDC.getCopyOfContextMap();

            return () -> {
                // 비동기 스레드 MDC 세팅
                if (mdcMap != null) {
                    MDC.setContextMap(mdcMap);
                } else {
                    MDC.clear();
                }

                try {
                    runnable.run();
                } finally {
                    // 스레드풀 스레드 정리
                    MDC.clear();
                }
            };
        };
    }

    // 요청 스레드의 SecurityContext를 비동기 스레드에 전달
    public TaskDecorator securityContextTaskDecorator() {
        return runnable -> {
            // 요청 스레드 SecurityContext 스냅샷
            SecurityContext securityContext = SecurityContextHolder.getContext();
            return () -> {
                SecurityContextHolder.setContext(securityContext);
                try {
                    runnable.run();
                } finally {
                    // 스레드풀 스레드 정리
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }
}
