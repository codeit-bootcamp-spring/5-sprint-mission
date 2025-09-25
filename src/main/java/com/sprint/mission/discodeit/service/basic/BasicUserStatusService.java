package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;
	private final UserStatusMapper userStatusMapper;

	@Transactional
	@Override
	public UserStatusDto create(UserStatusCreateRequest request) {
		UUID userId = request.userId();

		User user = userRepository.findById(userId)
			.orElseThrow(() ->
				UserNotFoundException.withId(userId.toString())
			);
		Optional.ofNullable(user.getStatus())
			.ifPresent(status -> {
				throw UserStatusAlreadyExistsException.withUserIds(userId.toString());
			});

		Instant lastActiveAt = request.lastActiveAt();
		UserStatus userStatus = new UserStatus(user, lastActiveAt);
		userStatusRepository.save(userStatus);
		return userStatusMapper.toDto(userStatus);
	}

	@Override
	public UserStatusDto find(UUID userStatusId) {
		return userStatusRepository.findById(userStatusId)
			.map(userStatusMapper::toDto)
			.orElseThrow(
				() -> UserStatusAlreadyExistsException.withIds(userStatusId.toString()));
	}

	@Override
	public List<UserStatusDto> findAll() {
		return userStatusRepository.findAll().stream()
			.map(userStatusMapper::toDto)
			.toList();
	}

	@Transactional
	@Override
	public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
		Instant newLastActiveAt = request.newLastActiveAt();

		UserStatus userStatus = userStatusRepository.findById(userStatusId)
			.orElseThrow(
				() -> UserStatusAlreadyExistsException.withIds(userStatusId.toString()));
		userStatus.update(newLastActiveAt);

		return userStatusMapper.toDto(userStatus);
	}

	@Transactional
	@Override
	public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
		Instant newLastActiveAt = request.newLastActiveAt();

		UserStatus userStatus = userStatusRepository.findByUserId(userId)
			.orElseThrow(
				() -> UserStatusAlreadyExistsException.withUserIds(userId.toString()));
		userStatus.update(newLastActiveAt);

		return userStatusMapper.toDto(userStatus);
	}

	@Transactional
	@Override
	public void delete(UUID userStatusId) {
		if (!userStatusRepository.existsById(userStatusId)) {
			throw UserStatusAlreadyExistsException.withIds(userStatusId.toString());
		}
		userStatusRepository.deleteById(userStatusId);
	}
}
