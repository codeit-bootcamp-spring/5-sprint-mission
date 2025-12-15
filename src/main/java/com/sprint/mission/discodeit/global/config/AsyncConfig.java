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

    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueCapacity;
    private final int awaitTerminationSeconds;
    private final String threadNamePrefix;

    public AsyncConfig(AsyncProperties asyncProperties) {
        corePoolSize = asyncProperties.corePoolSize();
        maxPoolSize = asyncProperties.maxPoolSize();
        queueCapacity = asyncProperties.queueCapacity();
        awaitTerminationSeconds = asyncProperties.awaitTerminationSeconds();
        threadNamePrefix = "Event-";
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);

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
