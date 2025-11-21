package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class DataSourceProxyConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource actualDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public DataSource proxyDataSource(DataSource actualDataSource) {
        return ProxyDataSourceBuilder
            .create(actualDataSource)
            .name("MyDS")
            .logQueryBySlf4j(SLF4JLogLevel.INFO)
            .logSlowQueryBySlf4j(5, TimeUnit.SECONDS)
            .multiline()
            .countQuery()
            .build();
    }
}
