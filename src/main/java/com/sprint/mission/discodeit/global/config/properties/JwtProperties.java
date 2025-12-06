package com.sprint.mission.discodeit.global.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties("discodeit.jwt")
@Validated
public record JwtProperties(
    @DefaultValue @Valid AccessToken accessToken,
    @DefaultValue @Valid RefreshToken refreshToken,
    @NotNull String refreshTokenCookieName,
    @Positive int maxSessions,
    @DefaultValue("in-memory") JwtRegistryType registryType
) {
    public record AccessToken(
        @NotBlank String secret,
        @DefaultValue("30m") Duration expiration,
        String previousSecret
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }

    public record RefreshToken(
        @NotBlank String secret,
        @DefaultValue("7d") Duration expiration,
        String previousSecret
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }
}
