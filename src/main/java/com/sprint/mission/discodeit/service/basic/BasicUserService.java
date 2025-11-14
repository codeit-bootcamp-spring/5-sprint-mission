package com.sprint.mission.discodeit.service.basic;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.Role;
import com.sprint.mission.discodeit.exception.user.DuplicateUserEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserNameException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserNameOrEmailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

	private final BinaryContentService binaryContentService;

	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final UserMapper userMapper;
	private final BinaryContentMapper binaryContentMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDto create(CreateUserDTO dto) {
		log.debug("user create 트랜잭션 시작");
		String username = dto.getUsername();
		String email = dto.getEmail();
		String password = passwordEncoder.encode(dto.getPassword());
		CreateBiContentDTO profileImage = dto.getBinaryContent();
		log.debug("user info username={}, email={}, password={},", username, email, password);

		// 1. Check username 과 email 중복 여부
		if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
			log.error("Username or email 중복");
			throw new DuplicateUserNameOrEmailException(Map.of("username", username, "email", email));
		}

		log.debug("start create user profile image username={}", username);
		// 2. Profile Image 저장
		BinaryContent userProfile = Optional.ofNullable(profileImage)
		  .map(binaryContentService::create)
		  .orElse(null);
		log.debug("success create user profile image username={}, profileID={}", username, userProfile);

		// 3. 인스턴스 생성
		User newUser = new User(
		  username,
		  email,
		  password,
		  userProfile,
		  Role.USER
		);
		userRepository.save(newUser);
		log.debug("success save userEntity  username={}", username);

		// 4. User Status 생성
		UserStatus userStatus = new UserStatus(newUser);
		userStatusRepository.save(userStatus);
		log.debug("success save userStatusEntity  userStatusID={}", userStatus.getId());

		log.debug("user create 트랜잭션 정상 종료");
		// 5. 데이터 저장
		return userMapper.toDto(newUser, userStatus.isOnline(), binaryContentMapper.toDto(newUser.getProfileImage()));
	}

	@Override
	@Transactional
	public void delete(UUID userId) {
		log.debug("user delete 트랜잭션 시작");

		User targetUser = userRepository.findById(userId)
		  .orElseThrow(() -> new UserNotFoundException(Map.of("id", userId)));

		// 1. User Status 삭제
		userStatusRepository.deleteByUserId(userId);
		log.debug("success delete userStatusEntity  userID={}", userId);

		// 2. Profile Image 삭제
		Optional<BinaryContent> profileImage = Optional.ofNullable(targetUser.getProfileImage());
		if (profileImage.isPresent() && binaryContentRepository.findById(profileImage.get().getId()).isPresent()) {
			binaryContentRepository.deleteById(targetUser.getProfileImage().getId());
			binaryContentStorage.put(targetUser.getProfileImage().getId(), null, null); // 스토리지에서 삭제
			log.debug("success delete userProfile  userID={}", userId);
		}
		// 3. User 삭제
		userRepository.deleteById(userId);
		log.debug("success delete userEntity userID={}", userId);

		log.debug("user delete 트랜잭션 정상 종료");
	}

	@Override
	@Transactional
	public UserDto update(UpdateUserDTO dto) {
		log.debug("user update 트랜잭션 시작");
		UUID userId = dto.getUserId();
		String newUsername = dto.getNewUsername();
		String newEmail = dto.getNewEmail();
		String newPassword = dto.getNewPassword();
		CreateBiContentDTO newProfileImage = dto.getNewProfilePicture();
		log.debug("new user info newUsername={}, newEmail={}, newPassword={} "
		  , newUsername, newEmail, newPassword);

		User targetUser = userRepository.findById(userId)
		  .orElseThrow(() -> new UserNotFoundException(Map.of("id", userId)));

		// 1. Validate And Change
		if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(targetUser.getUsername())) {
			if (userRepository.existsByUsername(newUsername)) {
				log.error("new username 중복");
				throw new DuplicateUserNameException();
			}
			targetUser.setUsername(newUsername);
		}
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(targetUser.getUsername())) {
			if (userRepository.existsByEmail(newEmail)) {
				log.error("new email 중복");
				throw new DuplicateUserEmailException();
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
			  binaryContentStorage.put(oldProfile.getId(), null, null); // 스토리지 삭제

			  BinaryContent savedProfile = binaryContentRepository.save(newProfileImage.toBinaryContent());
			  binaryContentStorage.put(savedProfile.getId(), profileContent.getContent(),
				parseMediaType(newProfileImage.getContentType())); // 스토리지 저장
			  log.debug("success store new user profile image username={}", newUsername);

			  targetUser.setProfileImage(savedProfile);
			  return savedProfile.getId();
		  });
		userRepository.save(targetUser);
		log.debug("success update userEntity  username={}", targetUser.getUsername());

		boolean isOnline = userStatusRepository.findByUserId(userId).map(UserStatus::isOnline).orElse(false);

		log.debug("user update 트랜잭션 정상 종료");
		return userMapper.toDto(targetUser, isOnline, binaryContentMapper.toDto(targetUser.getProfileImage()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> readAll() {
		List<User> users = userRepository.findUserDetailsAll();
		List<UUID> UserIds = users.stream().map(User::getId).toList();
		List<UserStatus> userStatuses = userStatusRepository.findByUserIdIn(UserIds);

		Map<UUID, Boolean> userID2IsOnlineMap = userStatuses.stream()
		  .collect(Collectors.toMap(
			us -> us.getUser().getId(), // key: User ID
			UserStatus::isOnline       // value: online 상태
		  ));

		return users.stream()
		  .map(
			u -> userMapper.toDto(u, userID2IsOnlineMap.get(u.getId()), binaryContentMapper.toDto(u.getProfileImage())))
		  .toList();
	}

}
