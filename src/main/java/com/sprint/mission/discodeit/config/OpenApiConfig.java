package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI discodeitOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Discodeit API 문서")
                        .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                        .version("v1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8080").description("로컬 서버")));
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")                                  // => /api-docs/v1
                .pathsToMatch("/api/**")                      // 스캔할 엔드포인트
                .build();
    }

}
