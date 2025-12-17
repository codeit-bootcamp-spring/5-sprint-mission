package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cores * 2);
        executor.setMaxPoolSize(cores * 4);
        executor.setQueueCapacity(cores * 50);
        executor.setThreadNamePrefix("async-event-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(
                new CompositeTaskDecorator(List.of(mdcTaskDecorator(), securityContextTaskDecorator())));
        executor.initialize();

        log.info("[AsyncConfig] 사용 가능한 프로세서 수: {}", cores);
        log.info("[AsyncConfig] 스레드 풀 설정 - Core: {}, Max: {}, Queue: {}", cores * 2, cores * 4,  cores * 50);

        return executor;
    }

    // TaskDecorator : 비동기 로직(@Async)이 동작 전과 후단에 필요 로직이나 데이터를 추가하는 인터페이스
    public TaskDecorator mdcTaskDecorator() {
        return task -> {
            // 현재 스레드(MVC, 요청 스레드)에서 활용하던 MDC를 전체 컨텍스트로 복사
            Map<String, String> context = MDC.getCopyOfContextMap();
            return ()->{
                // 새로 생긴 Thread 영역
                try {
                    if( context != null ){
                        // 비동기 스레드 MDC 컨텍스트 복사
                        MDC.setContextMap(context);
                    }
                    task.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }

    // SecurityContext 복사 TaskDecorator
    public TaskDecorator securityContextTaskDecorator() {
        return task -> {
            SecurityContext context = SecurityContextHolder.getContext();
            return ()->{
                try {
                    if(context != null){
                        SecurityContextHolder.setContext(context);
                    }
                    task.run();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }
}
