package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.admin")
@Validated
public record AdminProperties(
    boolean enabled,

    @NotBlank
    String username,

    @NotBlank
    @Email
    String email,

    @NotBlank
    String password
) {

}
