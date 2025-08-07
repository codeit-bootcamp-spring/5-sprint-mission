package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.userStatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UpdateUserStatusByUserIdRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
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
	public UserStatusResponse create(CreateUserStatusRequest request) {
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
	public UserStatusResponse update(UpdateUserStatusRequest request) {
		UserStatus userStatus = userStatusRepository.findById(request.getId())
			.orElseThrow(UserStatusNotFoundException::new);

		userStatus.updateUpdatedAt();
		userStatusRepository.save(userStatus);

		return UserStatusResponse.success(userStatus);
	}

	@Override
	public UserStatusResponse updateByUserId(UpdateUserStatusByUserIdRequest request) {
		UserStatus userStatus = userStatusRepository.findByUserId(request.getUserId())
			.orElseThrow(UserStatusNotFoundException::new);

		userStatus.updateUpdatedAt();
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
}