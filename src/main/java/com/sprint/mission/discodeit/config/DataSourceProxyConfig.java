package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.properties.DataSourceProxyProperties;
import com.sprint.mission.discodeit.util.SqlKeywordColorizer;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@Profile({"local", "test"})
@RequiredArgsConstructor
public class DataSourceProxyConfig {

    private final DataSourceProxyProperties proxyProperties;

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource rawDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean
    @Primary
    public DataSource proxyDataSource(DataSource rawDataSource) {
        Formatter sqlFormatter = new BasicFormatterImpl();
        return ProxyDataSourceBuilder
            .create(rawDataSource)
            .name(proxyProperties.name())
            .formatQuery(sql -> SqlKeywordColorizer.colorize(sqlFormatter.format(sql)))
            .logQueryBySlf4j(proxyProperties.logLevel())
            .logSlowQueryBySlf4j(
                proxyProperties.slowQueryThreshold().toMillis(),
                TimeUnit.MILLISECONDS,
                proxyProperties.slowQueryLogLevel())
            .countQuery()
            .build();
    }
}
