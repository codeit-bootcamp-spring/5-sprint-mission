package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateDefaultNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;

	@Override
	public UserResponse createUser(UserCreateRequest request) {
		if (userRepository.existsByLoginId(request.getUsername())) {
			throw new DuplicateLoginIdException();
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateEmailException();
		}

		User user;
		UUID profileId = null;

		if (request.getProfileImage() != null) {
			BinaryContent profileImage = request.getProfileImage().toBinaryContent();
			binaryContentRepository.save(profileImage);
			profileId = profileImage.getId();

			user = request.toUserWithProfile(profileId);
		} else {
			user = request.toUser();
		}

		userRepository.save(user);

		UserStatus userStatus = new UserStatus(user);
		userStatusRepository.save(userStatus);

		List<Channel> publicChannels = channelRepository.findAll().stream()
				.filter(channel -> "PUBLIC".equals(channel.getType()))
				.toList();

		for (Channel channel : publicChannels) {
			ReadStatus readStatus = new ReadStatus(user, channel);
			readStatusRepository.save(readStatus);
		}

		return UserResponse.success(user);
	}

	@Override
	public UserResponse findById(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		UserResponse userResponse = UserResponse.success(user);
		updateOnlineStatus(userResponse);
		return userResponse;
	}

	@Override
	public UserResponse findByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		UserResponse userResponse = UserResponse.success(user);
		updateOnlineStatus(userResponse);
		return userResponse;
	}


	@Override
	public List<UserResponse> findAll() {
		List<User> users = userRepository.findAll();
		List<UserResponse> userResponseList = new ArrayList<>();

		for (User user : users) {
			UserResponse userResponse = UserResponse.success(user);
			updateOnlineStatus(userResponse);
			userResponseList.add(userResponse);
		}

		return userResponseList;
	}

	@Override
	public UserResponse update(UUID id, UserUpdateRequest request,
			UserProfileImageRequest profileImageRequest) {

		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

		String newUsername = request.getNewUsername();
		String newEmail = request.getNewEmail();

		if (request.getNewUsername() != null && userRepository.existsByLoginId(newUsername)) {
			throw new DuplicateLoginIdException();
		}
		if (request.getNewEmail() != null && userRepository.existsByEmail(newEmail)) {
			throw new DuplicateEmailException();
		}

		if (request.getNewUsername() != null) {
			user.updateUsername(request.getNewUsername());
		}
		if (request.getNewEmail() != null) {
			user.updateEmail(request.getNewEmail());
		}
		if (request.getNewPassword() != null) {
			user.updatePassword(request.getNewPassword());
		}

		if (profileImageRequest != null) {
			BinaryContent newProfileImage = profileImageRequest.toBinaryContent();
			binaryContentRepository.save(newProfileImage);

			if (user.getProfileId() != null) {
				binaryContentRepository.deleteById(user.getProfileId());
			}

			user.updateProfileId(newProfileImage.getId());
		} else {
			if (user.getProfileId() != null) {
				binaryContentRepository.deleteById(user.getProfileId());
				user.removeProfile();
			}
		}

		userRepository.save(user);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse updateUserPassword(UUID userId, UserUpdatePasswordRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.getPassword().equals(request.getCurrentPassword())) {
			throw new InvalidPasswordException();
		}

		user.updatePassword(request.getNewPassword());

		userRepository.save(user);

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 Response 반환
		return UserResponse.success(user);
	}

	@Override
	public UserResponse updateUserDefalutNickname(UUID userId,
			UserUpdateDefaultNicknameRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		user.updateDefaultNickname(request.getNewNickname());

		userRepository.save(user);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse updateUserProfile(UUID userId, UserProfileImageRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		UUID oldProfileId = user.getProfileId();

		if (request != null) {
			// 새로운 프로필 이미지 제공 - 업데이트 (기존과 같아도 새로 저장)
			BinaryContent newProfileImage = request.toBinaryContent();
			binaryContentRepository.save(newProfileImage);

			// 기존 프로필 이미지가 있으면 삭제
			if (oldProfileId != null) {
				binaryContentRepository.deleteById(oldProfileId);
			}

			user.updateProfileId(newProfileImage.getId());
		} else {
			// null 제공 - 기존 프로필 제거
			if (oldProfileId != null) {
				binaryContentRepository.deleteById(oldProfileId);
				user.removeProfile();
			}
		}

		userRepository.save(user);

		return UserResponse.success(user);
	}

	@Override
	public UserDeleteResponse delete(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());


		return UserDeleteResponse.success(user);
	}

	@Override
	public UserDeleteResponse delete(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserStatusNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());

		return UserDeleteResponse.success(user);
	}

	private void updateOnlineStatus(UserResponse userResponse) {
		boolean online = userStatusRepository.findByUserId(userResponse.getId())
				.map(userStatus -> {
					Instant lastActiveAt = userStatus.getLastActiveAt();
					if (lastActiveAt == null) {
						return false;
					}
					return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
				})
				.orElse(false);

		userResponse.setOnline(online);
	}
}
