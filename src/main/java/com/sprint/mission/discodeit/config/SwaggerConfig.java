package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Discodeit API 문서")
            .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
            .version("v1.0.0")
            .contact(new Contact()
                .name("개발팀")
                .email("halogiju123@gmail.com")
                .url("https://github.com/halogiju123"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("로걸 개발 서버")
            ))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(new Components()
            .addSecuritySchemes("Bearer Authentication",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
            )
        )
        ;
  }
}
