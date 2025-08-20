package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.userstatus.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
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
	public UserStatusResponse create(UserStatusCreateRequest request) {
		if (!userRepository.existsById(request.getUserId())) {
			throw new UserNotFoundException();
		}

		if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
			throw new AlreadyExistsUserStatusException();
		}

		UserStatus userStatus = new UserStatus(request.getUserId());
		userStatusRepository.save(userStatus);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public UserStatusResponse getById(UUID id) {
		UserStatus userStatus = userStatusRepository.findById(id)
			.orElseThrow(UserStatusNotFoundException::new);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public List<UserStatusResponse> getAll() {
		List<UserStatus> userStatuses = userStatusRepository.findAll();

		return userStatuses.stream()
			.map(UserStatusResponse::success)
			.toList();
	}


	@Override
	public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
		UserStatus userStatus = userStatusRepository.findById(userStatusId)
			.orElseThrow(UserStatusNotFoundException::new);

		userStatus.updateLastActiveAt(request.getNewLastActiveAt());
		userStatusRepository.save(userStatus);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
		UserStatus userStatus = userStatusRepository.findByUserId(userId)
			.orElseThrow(UserStatusNotFoundException::new);

		userStatus.updateLastActiveAt(request.getNewLastActiveAt());
		userStatusRepository.save(userStatus);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public UserStatusResponse delete(UUID id) {
		UserStatus userStatus = userStatusRepository.findById(id)
			.orElseThrow(UserStatusNotFoundException::new);

		userStatusRepository.deleteById(id);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public boolean isOnline(UUID userId) {
		UserStatus userStatus = userStatusRepository.findByUserId(userId)
			.orElseThrow(UserStatusNotFoundException::new);

		Instant lastActiveAt = userStatus.getLastActiveAt();

		if (lastActiveAt == null) {
			return false;
		}

		return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
	}
}