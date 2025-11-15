package com.sprint.mission.discodeit.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Discodeit API 문서")
				.version("v1.0.0")
				.description("Discodeit 프로젝트의 Swagger API 문서입니다."))
			.servers(List.of(
				new Server().url("http://localhost:8080")
					.description("로컬 서버")
			));
	}

}
