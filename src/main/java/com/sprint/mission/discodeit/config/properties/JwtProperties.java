package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "discodeit.jwt")
@Validated
public record JwtProperties(
    @Valid AccessToken accessToken,
    @Valid RefreshToken refreshToken,
    @Positive(message = "Max sessions must be positive")
    int maxSessions
) {
    public record AccessToken(
        @NotBlank(message = "Access token secret must not be blank")
        String secret,
        String previousSecret,
        @Positive(message = "Access token expiration must be positive")
        int expirationMs
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }

    public record RefreshToken(
        @NotBlank(message = "Refresh token secret must not be blank")
        String secret,
        String previousSecret,
        @Positive(message = "Refresh token expiration must be positive")
        int expirationMs
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }
}
