package com.sprint.mission.discodeit.global.config;

import com.sprint.mission.discodeit.global.config.properties.DataSourceProxyProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

import static com.sprint.mission.discodeit.global.util.SqlKeywordColorizer.colorize;

@Configuration
@ConditionalOnProperty(prefix = "discodeit.datasource-proxy", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataSourceProxyConfig {

    private final DataSourceProxyProperties dataSourceProxyProperties;

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
            .name(dataSourceProxyProperties.name())
            .formatQuery(sql -> colorize(sqlFormatter.format(sql)))
            .logQueryBySlf4j(dataSourceProxyProperties.logLevel())
            .logSlowQueryBySlf4j(
                dataSourceProxyProperties.slowQueryThreshold().toMillis(),
                TimeUnit.MILLISECONDS,
                dataSourceProxyProperties.slowQueryLogLevel())
            .countQuery()
            .build();
    }
}
