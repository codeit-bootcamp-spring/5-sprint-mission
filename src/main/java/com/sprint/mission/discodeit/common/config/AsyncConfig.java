package com.sprint.mission.discodeit.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 20;
    private static final int QUEUE_CAPACITY = 500;
    private static final int AWAIT_TERMINATION_SECONDS = 30;

    @Bean
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("Event-");
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        executor.initialize();
        return executor;
    }

    private RejectedExecutionHandler rejectedExecutionHandler() {
        return (runnable, executor) -> {
            log.warn("Task rejected from eventTaskExecutor. Pool size: {}, Active: {}, Queue size: {}",
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size());
            new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(runnable, executor);
        };
    }
}
