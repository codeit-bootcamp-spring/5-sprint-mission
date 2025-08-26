package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UserDeleteResult;
import com.sprint.mission.discodeit.domain.dto.UserReadResult;
import com.sprint.mission.discodeit.domain.dto.UserUpdateResult;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.request.CreateUserResponse;
import com.sprint.mission.discodeit.domain.response.UserReadResponse;
import com.sprint.mission.discodeit.domain.response.UserUpdateResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

	private final BinaryContentService binaryContentService;

	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final UserStatusRepository userStatusRepository;

	@Override
	public User create(CreateUserDTO dto) {
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
		BinaryContent savedProfileImage = null;
		if (profileImage != null) {
			savedProfileImage = binaryContentService.create(profileImage);
		}
		UUID ProfileId = savedProfileImage != null ? savedProfileImage.getId() : null;

		// 3. 인스턴스 생성
		User newUser = new User(
		  username,
		  email,
		  password,
		  ProfileId
		);

		// 4. User Status 생성
		UserStatus userStatus = new UserStatus(newUser.getId());
		userStatus.setLastActiveAt(Instant.now()); // 현재 시간을 LastActiveAt으로 설정
		userStatusRepository.save(userStatus);

		// 5. 데이터 저장
		return userRepository.save(newUser);
	}

	@Override
	public UserDeleteResult delete(UUID userId) {
		User targetUser = userRepository.find(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		// 1. User Status 삭제
		userStatusRepository.deleteByUserId(userId);
		// 2. Profile Image 삭제
		if (binaryContentRepository.find(targetUser.getProfileId()).isPresent()) {
			binaryContentRepository.delete(targetUser.getProfileId());
		}
		// 3. User 삭제
		userRepository.delete(userId);

		return UserDeleteResult.builder()
		  .isDeleted(true)
		  .username(targetUser.getUsername())
		  .build();
	}

	@Override
	public UserUpdateResult update(UpdateUserDTO dto) {
		UUID userId = dto.getUserId();
		String newUsername = dto.getNewUsername();
		String newEmail = dto.getNewEmail();
		String newPassword = dto.getNewPassword();
		CreateBiContentDTO newProfileImage = dto.getNewProfilePicture();

		User targetUser = userRepository.find(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		if (newUsername != null) {
			targetUser.setUsername(newUsername);
		}
		if (newEmail != null) {
			targetUser.setEmail(newEmail);
		}
		if (newPassword != null) {
			targetUser.setPassword(newPassword);
		}

		Optional<BinaryContent> profilePicture = Optional.empty();
		if (newProfileImage != null) {
			// 기존 프로필이 있다면 삭제
			if (targetUser.getProfileId() != null) {
				binaryContentRepository.delete(targetUser.getProfileId());
			}
			profilePicture = Optional.of(binaryContentService.create(newProfileImage));
			targetUser.setProfileId(profilePicture.get().getId());

		}

		userRepository.save(targetUser);

		return UserUpdateResult.builder()
		  .id(targetUser.getId())
		  .createdAt(targetUser.getCreatedAt())
		  .updatedAt(targetUser.getUpdatedAt())
		  .username(targetUser.getUsername())
		  .email(targetUser.getEmail())
		  .profileId(profilePicture.map(BinaryContent::getId).orElse(null))
		  .build();
	}

	@Override
	public UserReadResult read(UUID userId) {
		User user = userRepository.find(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));

		Optional<UserStatus> status = userStatusRepository.findByUserId(userId);

		boolean isOnline = status.map(UserStatus::isOnline).orElse(false);

		return UserReadResult.builder()
		  .id(user.getId())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileId())
		  .online(isOnline)
		  .build();
	}

	@Override
	public List<UserReadResult> readAll() {
		return userRepository.findAll().stream().map(u -> UserReadResult.builder()
		  .id(u.getId())
		  .createdAt(u.getCreatedAt())
		  .updatedAt(u.getUpdatedAt())
		  .username(u.getUsername())
		  .email(u.getEmail())
		  .profileId(u.getProfileId())
		  .online(
			userStatusRepository.findByUserId(u.getId())
			  .map(UserStatus::isOnline)
			  .orElse(false))
		  .build()).toList();
	}

	@Override
	public boolean isEmpty(UUID userId) {
		return userRepository.isEmpty(userId);
	}

	@Override
	public void deleteAll() {
		userRepository.deleteAll();

	}

	public static CreateUserResponse toCreateUserResponse(User user) {
		return CreateUserResponse.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileId())
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
