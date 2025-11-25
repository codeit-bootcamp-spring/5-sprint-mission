package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class JwtTokenProvider {

  private final String accessSecret;
  private final String refreshSecret;
  private final long accessTokenValidityMillis;
  private final long refreshTokenValidityMillis;

  public JwtTokenProvider(String accessSecret, String refreshSecret, long accessTokenValidityMillis,
      long refreshTokenValidityMillis) {

    this.accessSecret = accessSecret;
    this.refreshSecret = refreshSecret;
    this.accessTokenValidityMillis = accessTokenValidityMillis;
    this.refreshTokenValidityMillis = refreshTokenValidityMillis;
  }

  public String createAccessToken(UUID userId, String username, String role) {

    Instant now = Instant.now();
    Instant exp = now.plusMillis(accessTokenValidityMillis);
    return createToken(userId, username, role, Date.from(now), Date.from(exp), "access",
        accessSecret);
  }

  public String createRefreshToken(UUID userId, String username, String role) {

    Instant now = Instant.now();
    Instant exp = now.plusMillis(refreshTokenValidityMillis);
    return createToken(userId, username, role, Date.from(now), Date.from(exp), "refresh",
        refreshSecret);
  }

  public Optional<JWTClaimsSet> parseAccessToken(String token) {
    return parseToken(token, accessSecret);
  }

  public Optional<JWTClaimsSet> parseRefreshToken(String token) {
    return parseToken(token, refreshSecret);
  }

  public boolean isAccessTokenExpired(String token) {
    return isExpired(token, accessSecret);
  }

  public boolean isRefreshTokenExpired(String token) {
    return isExpired(token, refreshSecret);
  }

  public String rotateRefreshToken(String oldRefreshToken) {

    Optional<JWTClaimsSet> claimsOpt = parseRefreshToken(oldRefreshToken);

    if (claimsOpt.isEmpty()) {
      throw new RuntimeException("Invalid refresh token");
    }

    JWTClaimsSet claims = claimsOpt.get();
    String sub = claims.getSubject();

    if (sub == null) {
      throw new RuntimeException("Invalid refresh token subject");
    }

    UUID userId;

    try {
      userId = UUID.fromString(sub);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid userId in token", e);
    }

    String username = (String) claims.getClaim("username");
    String role = (String) claims.getClaim("role");

    return createRefreshToken(userId, username, role);
  }

  private String createToken(UUID userId, String username, String role, Date iat, Date exp,
      String type, String secret) {

    try {
      JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().subject(String.valueOf(userId))
                                                               .issueTime(iat)
                                                               .expirationTime(exp)
                                                               .claim("type", type);

      if (username != null) {
        builder.claim("username", username);
      }

      if (role != null) {
        builder.claim("role", role);
      }

      JWTClaimsSet claims = builder.build();
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      MACSigner signer = new MACSigner(secret.getBytes());
      signedJWT.sign(signer);

      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("Failed to create token", e);
    }
  }

  private Optional<JWTClaimsSet> parseToken(String token, String secret) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      MACVerifier verifier = new MACVerifier(secret.getBytes());

      if (!signedJWT.verify(verifier)) {
        return Optional.empty();
      }

      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

      return Optional.ofNullable(claims);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private boolean isExpired(String token, String secret) {

    Optional<JWTClaimsSet> claims = parseToken(token, secret);

    if (claims.isEmpty()) {
      return true;
    }

    Date exp = claims.get()
                     .getExpirationTime();

    if (exp == null) {
      return true;
    }

    return exp.before(new Date());
  }

  public UUID getUserIdFromAccessToken(String token) {
    return parseAccessToken(token).map(claims -> UUID.fromString(claims.getSubject()))
                                  .orElseThrow(() -> new RuntimeException("Invalid token subject"));
  }

  public String getUsernameFromAccessToken(String token) {
    return parseAccessToken(token).map(claims -> (String) claims.getClaim("username"))
                                  .orElse(null);
  }

  public String getRoleFromAccessToken(String token) {
    return parseAccessToken(token).map(claims -> (String) claims.getClaim("role"))
                                  .orElse(null);
  }

  public boolean validateAccessToken(String token) {
    return parseAccessToken(token).isPresent() && !isAccessTokenExpired(token);
  }

  public UUID getUserIdFromRefreshToken(String token) {
    return parseRefreshToken(token).map(claims -> UUID.fromString(claims.getSubject()))
                                   .orElseThrow(
                                       () -> new RuntimeException("Invalid token subject"));
  }
}