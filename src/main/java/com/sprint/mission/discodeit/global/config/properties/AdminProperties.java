package com.sprint.mission.discodeit.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.Assert;

@ConfigurationProperties("discodeit.admin")
public record AdminProperties(
    @DefaultValue("false") boolean enabled,
    String username,
    String email,
    String password
) {
    public AdminProperties {
        if (enabled) {
            Assert.hasText(username, "discodeit.admin.username must not be empty when enabled is true");
            Assert.hasText(email, "discodeit.admin.email must not be empty when enabled is true");
            Assert.hasText(password, "discodeit.admin.password must not be empty when enabled is true");
        }
    }
}
