package com.sprint.mission.discodeit.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "discodeit.jwt")
public class JwtProperties {

    private final String secretKey;
    private final long accessTokenExpiration;  // 초 단위
    private final long refreshTokenExpiration; // 초 단위
    private final int maxActiveCount;
    private final String issuer;

    private final String registryType;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
}