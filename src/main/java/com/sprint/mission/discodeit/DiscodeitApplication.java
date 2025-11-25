package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
		System.out.println("localhost : http://localhost:8080");
		System.out.println("swagger : http://localhost:8080/swagger-ui/index.html");
	}
}