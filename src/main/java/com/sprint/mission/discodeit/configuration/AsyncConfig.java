package com.sprint.mission.discodeit.configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		int cores = Runtime.getRuntime().availableProcessors();
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(cores);
		executor.setMaxPoolSize(cores + 1);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("discodeit-async-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setTaskDecorator(
			new CompositeTaskDecorator(List.of(mdcTaskDecorator(), securityContextTaskDecorator()))
		);

		executor.initialize();
		return executor;
	}

	public TaskDecorator mdcTaskDecorator() {
		return task -> {
			Map<String, String> context = MDC.getCopyOfContextMap();
			return () -> {
				try {
					if (context != null) {
						MDC.setContextMap(context);
					}
					task.run();
				} finally {
					MDC.clear();
				}
			};
		};
	}

	public TaskDecorator securityContextTaskDecorator() {
		return task -> {
			SecurityContext context = SecurityContextHolder.getContext();
			return () -> {
				try {
					if (context != null) {
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
