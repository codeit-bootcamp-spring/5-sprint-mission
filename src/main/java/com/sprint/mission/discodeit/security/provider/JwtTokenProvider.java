package com.sprint.mission.discodeit.security.provider;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;

import jakarta.servlet.http.Cookie;

@Component
public class JwtTokenProvider {

	public static final String REQUEST_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

	private final long accessTokenValiditySeconds;
	private final long refreshTokenValiditySeconds;

	private String issuer;

	private final JWSSigner accessTokenSigner;
	private final JWSSigner refreshTokenSigner;

	private final JWSVerifier accessTokenVerifier;
	private final JWSVerifier refreshTokenVerifier;

	public JwtTokenProvider(
		@Value("${security.jwt.secret}") String secret,
		@Value("${security.jwt.access-token-validity-seconds}") long accessTokenValiditySeconds,
		@Value("${security.jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
		@Value("${security.jwt.issuer}") String issuer
	) throws JOSEException {
		this.issuer = issuer;
		this.accessTokenValiditySeconds = accessTokenValiditySeconds * 1000L;
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds * 1000L;

		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

		this.accessTokenSigner = new MACSigner(secretBytes);
		this.accessTokenVerifier = new MACVerifier(secretBytes);
		this.refreshTokenSigner = new MACSigner(secretBytes);
		this.refreshTokenVerifier = new MACVerifier(secretBytes);
	}

	public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
		return generateToken(userDetails, accessTokenValiditySeconds, accessTokenSigner, "ACCESS");
	}

	public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
		return generateToken(userDetails, refreshTokenValiditySeconds, refreshTokenSigner, "REFRESH");
	}

	public boolean validateAccessToken(String token) {
		return validateToken(token, accessTokenVerifier, "ACCESS");
	}

	public boolean validateRefreshToken(String token) {
		return validateToken(token, refreshTokenVerifier, "REFRESH");
	}

	public String getUsername(String token) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			return signedJWT.getJWTClaimsSet().getStringClaim("username");
		} catch (Exception e) {
			return null;
		}
	}

	private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);

			if (!signedJWT.verify(verifier))
				return false;
			if (!signedJWT.getJWTClaimsSet().getStringClaim("tokenType").equals(expectedType))
				return false;
			if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date()))
				return false;

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public Cookie generateRefreshTokenExpirationCookie() {
		Cookie cookie = new Cookie(REQUEST_TOKEN_COOKIE_NAME, "");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}

	private String generateToken(DiscodeitUserDetails userDetails, long expiration, JWSSigner signer,
		String tokenType) throws JOSEException {
		String tokenId = UUID.randomUUID().toString();
		UserDto user = userDetails.getUserDto();

		Date now = new Date();
		Date expireDate = new Date(now.getTime() + expiration);

		JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
			.subject(user.username())
			.jwtID(tokenId)
			.issuer(issuer)
			.claim("userId", user.id())
			.claim("username", user.username())
			.claim("tokenType", tokenType)
			.claim("roles", userDetails.getRole())
			.issueTime(now)
			.expirationTime(expireDate)
			.build();

		SignedJWT signedJWT = new SignedJWT(
			new JWSHeader(JWSAlgorithm.HS256), claimSet
		);

		signedJWT.sign(signer);
		return signedJWT.serialize();
	}

}
