package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private String name;
    private String version;
    private Server server;
    private List<String> admins;
    private Metadata metadata;

    @Getter
    @Setter
    public static class Server {
        private int port;
        private String url;
        private int timeout;
    }

    @Getter
    @Setter
    public static class Metadata {
        private String region;
        private String owner;
    }
}
