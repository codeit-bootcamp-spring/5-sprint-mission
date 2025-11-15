package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("userService")
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDto create(UserCommand command) {
		log.debug("[UserService#create] try: {}", command.forLog());

		String username = validateUsername(command.username());
		String password = passwordEncoder.encode(command.password());
		String email = validateEmail(command.email());
		BinaryContent profile = profileMapper(command.profile());

		User user = new User(username, email, password, profile);
		UserStatus userStatus = new UserStatus();
		userStatus.setLastActiveAt(Instant.now());
		user.attachStatus(userStatus);

		UserDto dto = userMapper.toDto(userRepository.save(user));

		log.info("[UserService#create] User created: {}", dto.forLog());

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto findById(UUID userId) {
		return userMapper.toDto(validateId(userId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> findAll() {
		return userRepository.findAll().stream()
			.map(userMapper::toDto)
			.toList();
	}

	@Override
	@Transactional
	public UserDto update(UUID userId, UserCommand command) {
		log.debug("[UserService#update] try id={}, command={}", userId, command.forLog());

		User user = validateId(userId);
		String newUserName = command.username();
		String newEmail = command.email();
		String newPassword = command.password();

		if (command.username() != null && !user.getUsername().equals(command.username())) {
			validateUsername(newUserName);
		}
		if (command.email() != null && !user.getEmail().equals(command.email())) {
			validateEmail(newEmail);
		}

		BinaryContent newProfile = profileMapper(command.profile());

		if (newProfile != null && user.getProfile() != null) {
			binaryContentRepository.deleteById(user.getProfile().getId());
			log.debug("[UserService#update] old profile deleted: {}", user.getProfile().getId());
		}

		user.update(newUserName, newEmail, newPassword, newProfile);
		UserDto dto = userMapper.toDto(userRepository.save(user));

		log.info("[UserService#update] User updated: {}", dto.forLog());

		return dto;
	}

	@Override
	@Transactional
	public void delete(UUID userId) {
		log.debug("[UserService#delete] try id={}", userId);
		User user = validateId(userId);

		if (user.getProfile() != null) {
			binaryContentRepository.deleteById(user.getProfile().getId());
			log.debug("[UserService#delete] profile deleted: {}", user.getProfile().getId());
		}

		userStatusRepository.findByUserId(user.getId())
			.ifPresent(userStatus -> {
				userStatusRepository.deleteById(userStatus.getId());
				log.debug("[UserService#delete] UserStatus deleted: {}", userStatus.getId());
			});

		userRepository.deleteById(user.getId());
		log.info("[UserService#delete] User deleted: id={}, username={}, email={}",
			user.getId(), user.getUsername(), LogUtils.maskEmail(user.getEmail()));
	}

	private BinaryContent profileMapper(Optional<NewBinaryContent> profile) {
		return profile.stream()
			.map(dto -> {
				BinaryContent binaryContent = new BinaryContent(
					dto.fileName(),
					dto.contentType(),
					dto.bytes().length);
				binaryContentRepository.save(binaryContent);
				binaryContentStorage.put(binaryContent.getId(), dto.bytes());
				return binaryContent;
			})
			.findFirst()
			.orElse(null);
	}

	private User validateId(UUID userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException().addDetail("userId", userId));
	}

	private String validateUsername(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new UserAlreadyExistsException().addDetail("username", username);
		}
		return username;
	}

	private String validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException().addDetail("email", email);
		}
		return email;
	}
}
