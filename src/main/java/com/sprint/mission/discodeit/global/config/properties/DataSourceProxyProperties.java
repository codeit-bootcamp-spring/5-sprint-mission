package com.sprint.mission.discodeit.global.config.properties;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("discodeit.datasource-proxy")
public record DataSourceProxyProperties(
    @DefaultValue("false") boolean enabled,
    @DefaultValue("DS-Proxy") String name,
    @DefaultValue("DEBUG") SLF4JLogLevel logLevel,
    @DefaultValue("500ms") Duration slowQueryThreshold,
    @DefaultValue("WARN") SLF4JLogLevel slowQueryLogLevel
) {
}
