package com.sprint.mission.discodeit.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private String username;
    private String password;
    private String email;
    private String nickname;
}