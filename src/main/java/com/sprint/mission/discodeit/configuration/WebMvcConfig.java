package com.sprint.mission.discodeit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean
	public MDCLoggingInterceptor MDCLoggingInterceptor() {
		return new MDCLoggingInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(MDCLoggingInterceptor())
			.addPathPatterns("/**");
	}
}
