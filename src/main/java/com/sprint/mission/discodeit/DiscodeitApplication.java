package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.IOException;

@EnableJpaAuditing
@PropertySource(value = "classpath:.env.properties", ignoreResourceNotFound = true)
@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}
