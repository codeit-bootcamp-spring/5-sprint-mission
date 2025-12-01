package com.sprint.mission.discodeit.security.jwt;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {
	// <userId, Queue<JwtInformation>>
	private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
	private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
	private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();
	private final int maxActiveJwtCount;
	private final JwtTokenProvider jwtTokenProvider;

	public InMemoryJwtRegistry(@Value("${discodeit.max-login}") int maxActiveJwtCount,
	  JwtTokenProvider jwtTokenProvider) {
		this.maxActiveJwtCount = maxActiveJwtCount;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void registerJwtInformation(JwtInformation jwtInformation) {
		UUID userId = jwtInformation.getUserDto().getId();
		insertOrigin(userId, jwtInformation);
	}

	@Override
	public void invalidateJwtInformationByUserId(UUID userId) {
		origin.get(userId).forEach((jwtInformation) -> {
			deleteOrigin(userId, jwtInformation);
		});
	}

	@Override
	public boolean hasActiveJwtInformationByUserId(UUID userId) {
		return Optional.ofNullable(origin.get(userId))
		  .map(queues -> !queues.isEmpty())
		  .orElse(false);
	}

	@Override
	public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
		return accessTokenIndexes.contains(accessToken);
	}

	@Override
	public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
		return refreshTokenIndexes.contains(refreshToken);
	}

	@Override
	public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
		// 1. 기존 토큰 전부 삭제
		UUID userId = newJwtInformation.getUserDto().getId();
		clearOrigin(userId);
		insertOrigin(userId, newJwtInformation);
	}

	@Scheduled(fixedDelay = 1000 * 60 * 5)
	@Override
	public void clearExpiredJwtInformation() {
		origin.values().forEach(queue ->
		  queue.removeIf(QEl ->
			{
				String accToken = QEl.getAccessToken();
				String refreshToken = QEl.getRefreshToken();

				boolean isExpired = !jwtTokenProvider.validateAccessToken(accToken) ||
				  !jwtTokenProvider.validateRefreshToken(refreshToken);

				if (isExpired) {
					accessTokenIndexes.remove(accToken);
					accessTokenIndexes.remove(refreshToken);
				}
				return isExpired;
			}
		  )
		);
	}

	private void insertOrigin(UUID key, JwtInformation jwtInformation) {
		origin.putIfAbsent(key, new ArrayDeque<>());

		Queue<JwtInformation> Q = origin.get(key);

		Q.add(jwtInformation);
		System.out.println("added " + jwtInformation);
		System.out.println(Q.size());
		while (Q.size() > maxActiveJwtCount) {
			JwtInformation jwtInfo = Q.poll();
			accessTokenIndexes.remove(jwtInfo.getAccessToken());
			refreshTokenIndexes.remove(jwtInfo.getRefreshToken());
		}

		accessTokenIndexes.add(jwtInformation.getAccessToken());
		refreshTokenIndexes.add(jwtInformation.getRefreshToken());
	}

	private void deleteOrigin(UUID key, JwtInformation jwtInformation) {
		String toDeleteAccToken = jwtInformation.getAccessToken();
		String toDeleteRefreshToken = jwtInformation.getRefreshToken();
		accessTokenIndexes.remove(toDeleteAccToken);
		accessTokenIndexes.remove(toDeleteRefreshToken);

		origin.get(key).removeIf(jwt -> jwt.equals(jwtInformation));

	}

	private void clearOrigin(UUID userId) {
		origin.get(userId).forEach(jwt
		  -> {
			String accessToken = jwt.getAccessToken();
			String refreshToken = jwt.getRefreshToken();
			accessTokenIndexes.remove(accessToken);
			refreshTokenIndexes.remove(refreshToken);
		});

		origin.get(userId).clear();

	}
}
