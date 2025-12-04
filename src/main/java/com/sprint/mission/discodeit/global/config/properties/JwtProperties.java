package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties("discodeit.jwt")
@Validated
public record JwtProperties(
    @Valid AccessToken accessToken,
    @Valid RefreshToken refreshToken,
    @Positive int maxSessions,
    @DefaultValue("in-memory") RegistryType registryType
) {
    public record AccessToken(
        @NotBlank String secret,
        String previousSecret,
        @DefaultValue("30m") Duration expiration
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }

    public record RefreshToken(
        @NotBlank String secret,
        String previousSecret,
        @DefaultValue("7d") Duration expiration
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }
}
