package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .openapi("3.1.0")
                .info(apiInfo())
                .servers(apiServers());
    }

    private Info apiInfo() {
        return new Info()
                .title("Discodeit API 문서")
                .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                .version("1.0.0");
    }

    private List<Server> apiServers() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 서버");

        return List.of(localServer);
    }
}
