package com.sprint.mission.discodeit.common.config;

import com.sprint.mission.discodeit.common.config.properties.AsyncProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncProperties asyncProperties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(asyncProperties.corePoolSize());
        executor.setMaxPoolSize(asyncProperties.maxPoolSize());
        executor.setQueueCapacity(asyncProperties.queueCapacity());
        executor.setAwaitTerminationSeconds(asyncProperties.awaitTerminationSeconds());
        executor.setThreadNamePrefix("Event-");

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setRejectedExecutionHandler(createRejectedExecutionHandler());

        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) ->
            log.error("[Async] Exception occurred in method: {}, params: {}",
                method.getName(), params, throwable);
    }

    private RejectedExecutionHandler createRejectedExecutionHandler() {
        return (runnable, executor) -> {
            log.warn("[Async] Task rejected. Pool: {}, Active: {}, Queue: {}. Executing in caller thread.",
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getQueue().size());

            if (!executor.isShutdown()) {
                new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(runnable, executor);
            }
        };
    }
}
