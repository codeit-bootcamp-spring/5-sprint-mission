package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateUserStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateStatusByUserIdDTO;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.response.UpdateUserStatusByUserIdResponse;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;
	private final UserStatusMapper userStatusMapper;

	@Override
	@Transactional
	public UserStatusDto create(CreateUserStatusDTO dto) {
		UUID userId = dto.getUserId();

		User user = userRepository.findById(userId).orElseThrow(() ->
		  new IllegalArgumentException("User ID cannot be null or empty")
		);

		UserStatus userStatus = new UserStatus(user);
		userStatusRepository.save(userStatus);
		return userStatusMapper.toDto(userStatus);
	}

	@Override
	@Transactional(readOnly = true)
	public UserStatusDto find(UUID id) {
		UserStatus userStatus = userStatusRepository.findById(id)
		  .orElseThrow(() -> new IllegalArgumentException("User status not found for ID: " + id));
		return userStatusMapper.toDto(userStatus);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserStatusDto> findAll() {
		return userStatusRepository.findAll().stream().map(userStatusMapper::toDto).toList();
	}

	@Override
	@Transactional
	public UserStatusDto updateStatusByUserId(UpdateStatusByUserIdDTO dto) {
		UUID userID = dto.getUserId();
		Instant newLastActiveAt = dto.getNewLastActiveAt();

		UserStatus userStatus = userStatusRepository.findByUserId(userID)
		  .orElseThrow(() -> new NoSuchElementException("User status not found for User ID: " + userID));

		userStatus.setLastActiveAt(newLastActiveAt);
		userStatusRepository.save(userStatus);

		return userStatusMapper.toDto(userStatus);

	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (userStatusRepository.existsById(id)) {
			throw new IllegalArgumentException("User status not found for ID: " + id);
		}
		userStatusRepository.deleteById(id);
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
