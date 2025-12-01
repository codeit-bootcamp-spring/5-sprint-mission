package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {
	/**
	 * 로그인 성공 시 JwtInformation을 등록합니다.
	 * 최대 동시 로그인 수(1)를 제어합니다.
	 * @param jwtInformation
	 */
	void registerJwtInformation(JwtInformation jwtInformation);

	/**
	 * UserId로 해당 유저의 모든 JwtInformation 정보를 삭제합니다.
	 * @param userId
	 */
	void invalidateJwtInformationByUserId(UUID userId);

	/**
	 * JwtInformation이 Registry에 존재하는지 확인합니다.
	 * @param userId
	 */
	boolean hasActiveJwtInformationByUserId(UUID userId);

	/**
	 * 필터에서 유효한 토큰인지 확인할 때 활용합니다.
	 * @param accessToken
	 */
	boolean hasActiveJwtInformationByAccessToken(String accessToken);

	/**
	 * 토큰 재발급 시 유효한 토큰인지 확인할 때 활용합니다.
	 * @param refreshToken
	 */
	boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

	/**
	 * 토큰 재발급 시 토큰 로테이션을 수행합니다.
	 * @param refreshToken
	 * @param newJwtInformation
	 */
	void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

	/**
	 * 만료된 JwtInformation을 삭제합니다.
	 */
	void clearExpiredJwtInformation();
}
