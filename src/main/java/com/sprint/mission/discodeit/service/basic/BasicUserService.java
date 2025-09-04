package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UserReadResult;
import com.sprint.mission.discodeit.domain.dto.UserUpdateResult;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.request.CreateUserResponse;
import com.sprint.mission.discodeit.domain.response.UserReadResponse;
import com.sprint.mission.discodeit.domain.response.UserUpdateResponse;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

	private final BinaryContentService binaryContentService;

	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public UserDto create(CreateUserDTO dto) {
		Optional.ofNullable(dto).orElseThrow(() -> new IllegalArgumentException("CreateUserDTO cannot be null"));
		String username = dto.getUsername();
		String email = dto.getEmail();
		String password = dto.getPassword();
		CreateBiContentDTO profileImage = dto.getBinaryContent();

		// 1. Check username 과 email 중복 여부
		if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("Username or email already exists");
		}

		// 2. Profile Image 저장
		BinaryContent userProfile = Optional.ofNullable(profileImage)
		  .map(binaryContentService::create)
		  .orElse(null);

		// 3. 인스턴스 생성
		User newUser = new User(
		  username,
		  email,
		  password,
		  userProfile
		);
		userRepository.save(newUser);

		// 4. User Status 생성
		UserStatus userStatus = new UserStatus(newUser);
		userStatusRepository.save(userStatus);

		// 5. 데이터 저장
		return userMapper.toDto(newUser, userStatus.isOnline());
	}

	@Override
	@Transactional
	public void delete(UUID userId) {
		User targetUser = userRepository.findById(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		// 1. User Status 삭제
		userStatusRepository.deleteByUserId(userId);
		// 2. Profile Image 삭제
		if (binaryContentRepository.findById(targetUser.getProfileImage().getId()).isPresent()) {
			binaryContentRepository.deleteById(targetUser.getProfileImage().getId());
			binaryContentStorage.put(targetUser.getProfileImage().getId(), null); // 스토리지에서 삭제
		}
		// 3. User 삭제
		userRepository.deleteById(userId);
	}

	@Override
	@Transactional
	public UserDto update(UpdateUserDTO dto) {
		UUID userId = dto.getUserId();
		String newUsername = dto.getNewUsername();
		String newEmail = dto.getNewEmail();
		String newPassword = dto.getNewPassword();
		CreateBiContentDTO newProfileImage = dto.getNewProfilePicture();

		User targetUser = userRepository.findById(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		// 1. Validate And Change
		if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(targetUser.getUsername())) {
			if (userRepository.existsByUsername(newUsername)) {
				throw new IllegalArgumentException("username with" + newUsername + "exist");
			}
			targetUser.setUsername(newUsername);
		}
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(targetUser.getUsername())) {
			if (userRepository.existsByEmail(newEmail)) {
				throw new IllegalArgumentException("email with" + newEmail + "exist");
			}
			targetUser.setEmail(newEmail);
		}
		if (newPassword != null && !newPassword.isBlank() && !newPassword.equals(targetUser.getUsername())) {
			targetUser.setPassword(newPassword);
		}

		// 2. 프로필 사진 업데이트
		BinaryContent oldProfile = targetUser.getProfileImage();
		Optional<UUID> newProfileId = Optional.ofNullable(newProfileImage)
		  .map((profileContent) -> {
			  binaryContentRepository.deleteById(oldProfile.getId());
			  binaryContentStorage.put(oldProfile.getId(), null); // 스토리지 삭제

			  BinaryContent savedProfile = binaryContentRepository.save(newProfileImage.toBinaryContent());
			  binaryContentStorage.put(savedProfile.getId(), profileContent.getContent()); // 스토리지 저장

			  targetUser.setProfileImage(savedProfile);
			  return savedProfile.getId();
		  });
		userRepository.save(targetUser);

		boolean isOnline = userStatusRepository.findByUserId(userId).map(UserStatus::isOnline).orElse(false);

		return userMapper.toDto(targetUser, isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto read(UUID userId) {
		User user = userRepository.findById(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));

		Optional<UserStatus> status = userStatusRepository.findByUserId(userId);

		boolean isOnline = status.map(UserStatus::isOnline).orElse(false);

		return userMapper.toDto(user, isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> readAll() {
		List<User> users = userRepository.findAll();
		List<UUID> UserIds = users.stream().map(User::getId).toList();
		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(UserIds);

		Map<UUID, Boolean> userID2IsOnlineMap = userStatuses.stream()
		  .collect(Collectors.toMap(
			us -> us.getUser().getId(), // key: User ID
			UserStatus::isOnline       // value: online 상태
		  ));

		return users.stream()
		  .map(u -> userMapper.toDto(u, userID2IsOnlineMap.get(u.getId())))
		  .toList();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID userId) {
		return userRepository.existsById(userId);
	}

	private UserReadResult toUserReadResult(User user, boolean isOnline) {
		return UserReadResult.builder()
		  .id(user.getId())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileImage() != null ? user.getProfileImage().getId() : null)
		  .online(isOnline)
		  .build();

	}

	@Override
	@Transactional(readOnly = true)
	public boolean isOnline(UUID id) {
		UserStatus userStatus = userStatusRepository.findByUserId(id)
		  .orElseThrow(() -> new NoSuchElementException("userStatus with ID" + id + "not found"));
		return userStatus.isOnline();
	}

	public static CreateUserResponse toCreateUserResponse(User user) {
		return CreateUserResponse.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileImage() != null ? user.getProfileImage().getId() : null)
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .build();
	}

	public static UserReadResponse toUserReadResponse(UserReadResult user) {
		return UserReadResponse.builder()
		  .id(user.getId())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileId())
		  .online(user.isOnline())
		  .build();
	}

	public static UserUpdateResponse toUserUpdateResponse(UserUpdateResult user) {
		return UserUpdateResponse.builder()
		  .id(user.getId())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileId())
		  .build();
	}

}
