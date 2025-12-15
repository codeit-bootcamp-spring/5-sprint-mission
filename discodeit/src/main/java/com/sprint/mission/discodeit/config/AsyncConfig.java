package com.sprint.mission.discodeit.config;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync // @Async 어노테이션을 활성화 하기 위한 중요한 어노테이션
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final MeterRegistry meterRegistry;

    // CPU 바운드 작업용 Executor
    //  - 예 : 이미지 처리(썸네일), 데이터 변환, 암호화, 복호화 등등 CPU 자원을 많이 사용 하는 작업
    //  - 코어 수 기준으로 설정
    @Bean(name = "cpuExecutor")
    public ThreadPoolTaskExecutor cpuExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores); // 코어수 만큼
        executor.setMaxPoolSize(cores + 1); // 코어 + 1
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("cpu-async-");
        // pool 다찼을 경우의 거부 정책
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(
                new CompositeTaskDecorator(List.of(mdcTaskDecorator(), securityContextTaskDecorator())));

        executor.initialize();

        // Micrometer 등록(모니터링용)
        ExecutorServiceMetrics.monitor(meterRegistry,
                executor.getThreadPoolExecutor(),
                "cpu-executor");

        return executor;
    }

    // IO 바운드 작업용 Executor
    //  - 예 : 이메일 발송, 외부 API 호출, 파일 IO, DB 대량 조회 등등
    //  - 대기 시간이 많으므로 CPU보다 더 많은 스레드 허용
    @Bean(name = "ioExecutor")
    public ThreadPoolTaskExecutor ioExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores * 2); // 최소 CPU 2배수
        executor.setMaxPoolSize(cores * 4); // 많으면 4~5배수
        executor.setQueueCapacity(500); // IO 널널히
        executor.setThreadNamePrefix("io-async-");
        // pool 다찼을 경우의 거부 정책
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(
                new CompositeTaskDecorator(List.of(mdcTaskDecorator(), securityContextTaskDecorator())));

        executor.initialize();

        // Micrometer 등록(모니터링용)
        ExecutorServiceMetrics.monitor(meterRegistry,
                executor.getThreadPoolExecutor(),
                "io-executor");
        return executor;
    }

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(
                new CompositeTaskDecorator(List.of(mdcTaskDecorator(), securityContextTaskDecorator())));
        executor.initialize();
        ExecutorServiceMetrics.monitor(meterRegistry,
                executor.getThreadPoolExecutor(),
                "event-executor");
        return executor;
    }

    // void 반환용 @Async 메서드 예외처리 핸들러 적용
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new GlobalAsyncExceptionHandler();
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
