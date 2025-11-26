package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class AppConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// Java 8 날짜/시간 직렬화 지원
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// 직렬화 시 null 필드 제외
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		// 한글 깨짐 방지, UTF-8로만 직렬화됨
		mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

		// enum 값을 String 으로 직렬화
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

		return mapper;
	}
}
