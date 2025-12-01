package com.sprint.mission.discodeit.security;

import static java.util.stream.Collectors.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {

	private final MessageRepository messageRepository;
	private final SessionRegistry sessionRegistry;
	private final JwtRegistry jwtRegistry;

	public boolean isMessageOwner(UUID messageId, UUID userId) {
		return messageRepository.findById(messageId)
		  .map(message -> message
			.getUser()
			.getId()
			.equals(userId))
		  .orElse(false);
	}

	public Map<UUID, Boolean> getUserId2OnlineMap(Set<UUID> userIds) {
		return userIds.stream().collect(toMap(
		  id -> id,
		  this::isOnline
		));

	}

	public boolean isOnline(User user) {
		UUID userId = user.getId();
		return isOnline(userId);
	}

	public boolean isOnline(UUID userId) {
		return jwtRegistry.hasActiveJwtInformationByUserId(userId);
	}
}
