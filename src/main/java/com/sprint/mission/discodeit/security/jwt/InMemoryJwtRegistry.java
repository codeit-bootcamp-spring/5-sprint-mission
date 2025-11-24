package com.sprint.mission.discodeit.security.jwt;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.user.JwtInformation;

import lombok.RequiredArgsConstructor;

// JWT 기반 세션 관리 인터페이스 구현, 블랙리스트가 아닌 화이트리스트 개념!!
// -> 레디스로 구현하면 더 좋은 부분인데, 그건 다음에 리팩토링 될 예정
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry<UUID> {

	// <userId, Queue<JwtInformation>>
	private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
	private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
	private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

	private final int maxActiveJwtCount;
	private final JwtTokenProvider jwtTokenProvider;

	// 새 JWT 정보를 registry에 등록하고 maxActiveJwtCount 초과 시 가장 오래된 토큰 제거
	@Override
	public void registerJwtInformation(JwtInformation jwtInformation) {
		origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
			if (queue == null) {
				queue = new ConcurrentLinkedQueue<>();
			}
			// If the queue exceeds the max size, remove the oldest token
			if (queue.size() >= maxActiveJwtCount) {
				JwtInformation deprecatedJwtInformation = queue.poll();// Remove the oldest token
				if (deprecatedJwtInformation != null) {
					removeTokenIndex(
						deprecatedJwtInformation.getAccessToken(),
						deprecatedJwtInformation.getRefreshToken()
					);
				}
			}

			// 새 토큰 추가
			queue.add(jwtInformation); // Add the new token
			addTokenIndex(
				jwtInformation.getAccessToken(),
				jwtInformation.getRefreshToken()
			);
			return queue;
		});
	}

	// 특정 userId의 모든 JWT를 무효화하고 registry에서 제거
	@Override
	public void invalidateJwtInformationByUserId(UUID userId) {
		origin.computeIfPresent(userId, (key, queue) -> {
			queue.forEach(jwtInformation -> {
				removeTokenIndex(
					jwtInformation.getAccessToken(),
					jwtInformation.getRefreshToken()
				);
			});
			queue.clear(); // Clear the queue for this user
			return null; // Remove the user from the registry
		});
	}

	// 특정 userId가 활성 JWT를 가지고 있는지 여부 확인
	@Override
	public boolean hasActiveJwtInformationByUserId(UUID userId) {
		return origin.containsKey(userId);
	}

	// Access Token이 registry에 등록된 활성 토큰인지 확인
	@Override
	public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
		return accessTokenIndexes.contains(accessToken);
	}

	// Refresh Token이 registry에 등록된 활성 토큰인지 확인
	@Override
	public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
		return refreshTokenIndexes.contains(refreshToken);
	}

	// Refresh Token을 기준으로 기존 JWT 정보를 새 JWT로 교체(토큰 회전)
	@Override
	public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
		origin.computeIfPresent(newJwtInformation.getUserDto().id(), (key, queue) -> {
			queue.stream().filter(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken))
				.findFirst()
				.ifPresent(jwtInformation -> {
					removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
					jwtInformation.rotate(
						newJwtInformation.getAccessToken(),
						newJwtInformation.getRefreshToken()
					);
					addTokenIndex(
						newJwtInformation.getAccessToken(),
						newJwtInformation.getRefreshToken()
					);
				});
			return queue;
		});
	}

	// 스케줄러, 만료된 JWT는 자동으로 정리하는 청소 작업(주기 5분)
	@Scheduled(fixedDelay = 1000 * 60 * 5)
	@Override
	public void clearExpiredJwtInformation() {
		origin.entrySet().removeIf(entry -> {
			Queue<JwtInformation> queue = entry.getValue();
			queue.removeIf(jwtInformation -> {
				boolean isExpired =
					!jwtTokenProvider.validateAccessToken(jwtInformation.getAccessToken()) ||
						!jwtTokenProvider.validateRefreshToken(jwtInformation.getRefreshToken());
				if (isExpired) {
					removeTokenIndex(
						jwtInformation.getAccessToken(),
						jwtInformation.getRefreshToken()
					);
				}
				return isExpired;
			});
			return queue.isEmpty(); // Remove the entry if the queue is empty
		});
	}

	// 토큰 추가하기
	private void addTokenIndex(String accessToken, String refreshToken) {
		accessTokenIndexes.add(accessToken);
		refreshTokenIndexes.add(refreshToken);
	}

	// 토큰 제거하기
	private void removeTokenIndex(String accessToken, String refreshToken) {
		accessTokenIndexes.remove(accessToken);
		refreshTokenIndexes.remove(refreshToken);
	}
}
