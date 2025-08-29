package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.CreateUserStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateStatusByUserIdDTO;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.response.UpdateUserStatusByUserIdResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;

	@Override
	public UserStatus create(CreateUserStatusDTO dto) {
		UUID userId = dto.getUserId();

		if (userId == null || userRepository.isEmpty(userId)) {
			throw new IllegalArgumentException("User ID cannot be null or empty");
		}

		UserStatus userStatus = new UserStatus(userId);
		return userStatusRepository.save(userStatus);
	}

	@Override
	public UserStatus find(UUID id) {
		return userStatusRepository.find(id)
		  .orElseThrow(() -> new IllegalArgumentException("User status not found for ID: " + id));
	}

	@Override
	public List<UserStatus> findAll() {
		return userStatusRepository.findAll();
	}

	@Override
	public UserStatus updateStatusByUserId(UpdateStatusByUserIdDTO dto) {
		UUID userID = dto.getUserId();
		Instant newLastActiveAt = dto.getNewLastActiveAt();

		UserStatus userStatus = userStatusRepository.findByUserId(userID)
		  .orElseThrow(() -> new NoSuchElementException("User status not found for User ID: " + userID));

		userStatus.setLastActiveAt(newLastActiveAt);

		return userStatusRepository.save(userStatus);

	}

	@Override
	public void delete(UUID id) {
		if (userStatusRepository.isEmpty(id)) {
			throw new IllegalArgumentException("User status not found for ID: " + id);
		}
		userStatusRepository.delete(id);
	}

	public static UpdateUserStatusByUserIdResponse toUpdateUserStatusByUserIdResponse(UserStatus userStatus) {
		return UpdateUserStatusByUserIdResponse.builder()
		  .id(userStatus.getId())
		  .createdAt(userStatus.getCreatedAt())
		  .updatedAt(userStatus.getUpdatedAt())
		  .userId(userStatus.getUser().getId())
		  .lastActiveAt(userStatus.getLastActiveAt())
		  .isOnline(userStatus.isOnline())
		  .build();
	}

}
