package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final BinaryContentRepository binaryContentRepository;
	private final BinaryContentStorage binaryContentStorage;

	@Transactional
	@Override
	public UserDto create(UserCreateRequest userCreateRequest,
		Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
		String username = userCreateRequest.username();
		String email = userCreateRequest.email();

		if (userRepository.existsByEmail(email)) {
			throw UserNotFoundException.withEmail(email);
		}
		if (userRepository.existsByUsername(username)) {
			throw UserNotFoundException.withUsername(username);
		}

		BinaryContent nullableProfile = optionalProfileCreateRequest
			.map(profileRequest -> {
				String fileName = profileRequest.fileName();
				String contentType = profileRequest.contentType();
				byte[] bytes = profileRequest.bytes();
				BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length,
					contentType);
				binaryContentRepository.save(binaryContent);
				binaryContentStorage.put(binaryContent.getId(), bytes);
				return binaryContent;
			})
			.orElse(null);
		String password = userCreateRequest.password();

		User user = new User(username, email, password, nullableProfile);
		Instant now = Instant.now();
		UserStatus userStatus = new UserStatus(user, now);

		userRepository.save(user);
		return userMapper.toDto(user);
	}

	@Override
	public UserDto find(UUID userId) {
		return userRepository.findById(userId)
			.map(userMapper::toDto)
			.orElseThrow(() -> UserNotFoundException.withId(userId.toString()));
	}

	@Override
	public List<UserDto> findAll() {
		return userRepository.findAllWithProfileAndStatus()
			.stream()
			.map(userMapper::toDto)
			.toList();
	}

	@Transactional
	@Override
	public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
		Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> UserNotFoundException.withId(userId.toString()));

		String newUsername = userUpdateRequest.newUsername();
		String newEmail = userUpdateRequest.newEmail();
		if (userRepository.existsByEmail(newEmail)) {
			throw UserNotFoundException.withEmail(newEmail);
		}
		if (userRepository.existsByUsername(newUsername)) {
			throw UserNotFoundException.withUsername(newUsername);
		}

		BinaryContent nullableProfile = optionalProfileCreateRequest
			.map(profileRequest -> {

				String fileName = profileRequest.fileName();
				String contentType = profileRequest.contentType();
				byte[] bytes = profileRequest.bytes();
				BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length,
					contentType);
				binaryContentRepository.save(binaryContent);
				binaryContentStorage.put(binaryContent.getId(), bytes);
				return binaryContent;
			})
			.orElse(null);

		String newPassword = userUpdateRequest.newPassword();
		user.update(newUsername, newEmail, newPassword, nullableProfile);

		return userMapper.toDto(user);
	}

	@Transactional
	@Override
	public void delete(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw UserNotFoundException.withId(userId.toString());
		}

		userRepository.deleteById(userId);
	}
}
