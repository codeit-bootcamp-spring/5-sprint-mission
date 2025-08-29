package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UserDeleteResult;
import com.sprint.mission.discodeit.domain.dto.UserReadResult;
import com.sprint.mission.discodeit.domain.dto.UserUpdateResult;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileUserService implements UserService {

	private final FileUserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

	private final BinaryContentService binaryContentService;

	@Override
	public User create(CreateUserDTO dto) {
		String username = dto.getUsername();
		String email = dto.getEmail();
		String password = dto.getPassword();

		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email cannot be null or empty");
		}
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}

		return userRepository.save(new User(username, email, password, null));
	}

	@Override
	public UserDeleteResult delete(UUID userId) {
		User targetUser = userRepository.find(userId)
		  .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		// 1. User Status 삭제
		userStatusRepository.deleteByUserId(userId);
		// 2. Profile Image 삭제
		if (binaryContentRepository.find(targetUser.getProfileImage().getId()).isPresent()) {
			binaryContentRepository.delete(targetUser.getProfileImage().getId());
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
		Optional.ofNullable(dto).orElseThrow(() -> new IllegalArgumentException("UpdateUserDTO cannot be null"));
		UUID userId = dto.getUserId();
		String newUsername = dto.getNewUsername();
		String newEmail = dto.getNewEmail();
		String newPassword = dto.getNewPassword();
		CreateBiContentDTO newProfileImage = dto.getNewProfilePicture();

		if (newUsername == null || newUsername.isEmpty()) {
			throw new IllegalArgumentException("New username cannot be null or empty");
		}
		if (newEmail == null || newEmail.isEmpty()) {
			throw new IllegalArgumentException("New email cannot be null or empty");
		}
		if (newPassword == null || newPassword.isEmpty()) {
			throw new IllegalArgumentException("New password cannot be null or empty");
		}

		User targetUser = userRepository.find(userId)
		  .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

		targetUser.setUsername(newUsername);
		targetUser.setEmail(newEmail);
		targetUser.setPassword(newPassword);

		if (newProfileImage != null) {
			// 기존 프로필이 있다면 삭제
			if (targetUser.getProfileImage().getId() != null) {
				binaryContentRepository.delete(targetUser.getProfileImage().getId());
			}
			BinaryContent newProfilePicture = binaryContentService.create(newProfileImage);
			targetUser.setProfileImage(newProfilePicture);

			userRepository.save(targetUser);
			return UserUpdateResult.builder()
			  .id(targetUser.getId())
			  .createdAt(targetUser.getCreatedAt())
			  .updatedAt(targetUser.getUpdatedAt())
			  .username(targetUser.getUsername())
			  .email(targetUser.getEmail())
			  .profileId(newProfilePicture.getId())
			  .build();
		}

		userRepository.save(targetUser);
		BinaryContent profilePicture = binaryContentRepository.find(targetUser.getProfileImage().getId())
		  .orElse(null);
		return UserUpdateResult.builder()
		  .id(targetUser.getId())
		  .createdAt(targetUser.getCreatedAt())
		  .updatedAt(targetUser.getUpdatedAt())
		  .username(targetUser.getUsername())
		  .email(targetUser.getEmail())
		  .profileId(profilePicture.getId())

		  .build();
	}

	@Override
	public UserReadResult read(UUID userId) {
		User user = userRepository.find(userId)
		  .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

		Optional<UserStatus> status = userStatusRepository.findByUserId(userId);

		boolean isOnline = status.stream()
		  .anyMatch(UserStatus::isOnline);

		return UserReadResult.builder()
		  .id(user.getId())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .profileId(user.getProfileImage().getId())
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
		  .profileId(u.getProfileImage().getId())
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
}
