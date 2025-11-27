package com.sprint.mission.discodeit.config.properties;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "discodeit.datasource-proxy")
public record DataSourceProxyProperties(
    String name,
    SLF4JLogLevel logLevel,
    Duration slowQueryThreshold,
    SLF4JLogLevel slowQueryLogLevel
) {
    public DataSourceProxyProperties {
        if (name == null) {
            name = "DS-Proxy";
        }
        if (logLevel == null) {
            logLevel = SLF4JLogLevel.DEBUG;
        }
        if (slowQueryThreshold == null) {
            slowQueryThreshold = Duration.ofMillis(500);
        }
        if (slowQueryLogLevel == null) {
            slowQueryLogLevel = SLF4JLogLevel.WARN;
        }
    }
}
