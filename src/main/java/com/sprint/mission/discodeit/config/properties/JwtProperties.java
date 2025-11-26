package com.sprint.mission.discodeit.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "discodeit.jwt")
@Validated
public record JwtProperties(
    @Valid AccessToken accessToken,
    @Valid RefreshToken refreshToken
) {

    public record AccessToken(
        @NotBlank(message = "Access token secret must not be blank")
        String secret,

        @Positive(message = "Access token expiration must be positive")
        int expirationMs
    ) {

    }

    public record RefreshToken(
        @NotBlank(message = "Refresh token secret must not be blank")
        String secret,

        @Positive(message = "Refresh token expiration must be positive")
        int expirationMs
    ) {

    }

}
