package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties("discodeit.jwt")
@Validated
public record JwtProperties(
    @Valid AccessToken accessToken,
    @Valid RefreshToken refreshToken,
    @Positive(message = "discodeit.jwt.max-sessions must be positive")
    int maxSessions
) {
    public record AccessToken(
        @NotBlank String secret,
        String previousSecret,
        @Positive int expirationMs
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }

    public record RefreshToken(
        @NotBlank String secret,
        String previousSecret,
        @Positive int expirationMs
    ) {
        public boolean hasPreviousSecret() {
            return hasText(previousSecret);
        }
    }
}
