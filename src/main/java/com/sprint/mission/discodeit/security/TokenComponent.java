package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JWSSigner;

public record TokenComponent(long expirationMs, JWSSigner signer, String tokenType) {

	public static TokenComponent of(long expirationMs, JWSSigner signer, String tokenType) {
		return new TokenComponent(expirationMs, signer, tokenType);
	}
}
