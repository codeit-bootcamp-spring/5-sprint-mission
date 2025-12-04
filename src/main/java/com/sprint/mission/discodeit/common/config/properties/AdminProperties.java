package com.sprint.mission.discodeit.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties("discodeit.admin")
public record AdminProperties(
    boolean enabled,
    String username,
    String email,
    String password
) {
    public AdminProperties {
        if (enabled) {
            if (!hasText(username)) {
                throw new IllegalArgumentException("discodeit.admin.username must not be empty");
            }
            if (!hasText(email)) {
                throw new IllegalArgumentException("discodeit.admin.email must not be empty");
            }
            if (!hasText(password)) {
                throw new IllegalArgumentException("discodeit.admin.password must not be empty");
            }
        }
    }
}
