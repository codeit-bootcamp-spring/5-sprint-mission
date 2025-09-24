package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateUserStatusDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateStatusByUserIdDTO;
import com.sprint.mission.discodeit.domain.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
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
		  new UserNotFoundException(Map.of("userId", userId))
		);

		UserStatus userStatus = new UserStatus(user);
		userStatusRepository.save(userStatus);
		return userStatusMapper.toDto(userStatus, user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserStatusDto find(UUID id) {
		UserStatus userStatus = userStatusRepository.findById(id)
		  .orElseThrow(() -> new UserStatusNotFoundException(Map.of("id", id)));
		return userStatusMapper.toDto(userStatus, userStatus.getUser());
	}

	@Override
	@Transactional
	public UserStatusDto updateStatusByUserId(UpdateStatusByUserIdDTO dto) {
		UUID userID = dto.getUserId();
		Instant newLastActiveAt = dto.getNewLastActiveAt();

		UserStatus userStatus = userStatusRepository.findByUserId(userID)
		  .orElseThrow(() -> new UserStatusNotFoundException(Map.of("userID", userID)));

		userStatus.setLastActiveAt(newLastActiveAt);
		userStatusRepository.save(userStatus);

		return userStatusMapper.toDto(userStatus, userStatus.getUser());

	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (userStatusRepository.existsById(id)) {
			throw new UserStatusNotFoundException(Map.of("id", id));
		}
		userStatusRepository.deleteById(id);
	}

}
