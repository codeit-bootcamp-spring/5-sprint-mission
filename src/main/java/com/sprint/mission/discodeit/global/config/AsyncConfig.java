package com.sprint.mission.discodeit.global.config;

import com.sprint.mission.discodeit.global.config.properties.AsyncProperties;
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
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    private final int CORE_POOL_SIZE;
    private final int MAX_POOL_SIZE;
    private final int QUEUE_CAPACITY;
    private final int AWAIT_TERMINATION_SECONDS;
    private final String THREAD_NAME_PREFIX;

    public AsyncConfig(AsyncProperties asyncProperties) {
        CORE_POOL_SIZE = asyncProperties.corePoolSize();
        MAX_POOL_SIZE = asyncProperties.maxPoolSize();
        QUEUE_CAPACITY = asyncProperties.queueCapacity();
        AWAIT_TERMINATION_SECONDS = asyncProperties.awaitTerminationSeconds();
        THREAD_NAME_PREFIX = "Event-";
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

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
