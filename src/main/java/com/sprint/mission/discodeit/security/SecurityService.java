package com.sprint.mission.discodeit.security;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.user.UserSessionStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {

	private final MessageRepository messageRepository;
	private final SessionRegistry sessionRegistry;

	public boolean isMessageOwner(UUID messageId, UUID userId) {
		return messageRepository.findById(messageId)
		  .map(message -> message
			.getUser()
			.getId()
			.equals(userId))
		  .orElse(false);
	}

	public Map<UUID, Boolean> getUserId2OnlineMap(Set<UUID> userIds) {

		final long ONLINE_THRESHOLD_MS = 5 * 60 * 1000L;
		long now = System.currentTimeMillis();

		// 1) 모든 UserDetails 추출
		List<DiscodeitUserDetails> allUsers = sessionRegistry.getAllPrincipals().stream()
		  .filter(p -> p instanceof DiscodeitUserDetails)
		  .filter(p -> userIds
			.contains(
			  ((DiscodeitUserDetails)p).getUserDto().getId()
			)
		  )
		  .map(p -> (DiscodeitUserDetails)p)
		  .toList();

		// 2) 각 유저별 모든 세션 정보 수집
		List<UserSessionStatus> allSessions = allUsers.stream()
		  .flatMap(user -> sessionRegistry.getAllSessions(user, false).stream()
			.map(si -> new UserSessionStatus(
			  user.getUserId(),
			  si.getLastRequest().getTime(),
			  now - si.getLastRequest().getTime() <= ONLINE_THRESHOLD_MS
			))
		  )
		  .toList();

		// 3) 유저별 최신 세션만 선택
		Map<UUID, UserSessionStatus> latestSessionPerUser = allSessions.stream()
		  .collect(toMap(
			UserSessionStatus::userID,
			Function.identity(),
			BinaryOperator.maxBy(Comparator.comparingLong(UserSessionStatus::lastRequest)))
		  );

		// 4) userId 별 최종값 반환
		return userIds.stream().collect(toMap(
		  id -> id,
		  id -> latestSessionPerUser.containsKey(id) && latestSessionPerUser.get(id).online()
		));

	}

	public boolean isOnline(User user) {
		final long ONLINE_THRESHOLD_MS = 5 * 60 * 1000L; // 5분(5 * 60초 * 1000ms)
		long now = System.currentTimeMillis();

		return sessionRegistry.getAllPrincipals().stream()
		  .filter(principal -> principal instanceof DiscodeitUserDetails)
		  .map(principal -> (DiscodeitUserDetails)principal)
		  .filter(details -> details.getUsername().equals(user.getUsername()))
		  .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
		  .anyMatch(sessionInfo -> {
			  long lastRequest = sessionInfo.getLastRequest().getTime();
			  return (now - lastRequest) <= ONLINE_THRESHOLD_MS;
		  });
	}
}
