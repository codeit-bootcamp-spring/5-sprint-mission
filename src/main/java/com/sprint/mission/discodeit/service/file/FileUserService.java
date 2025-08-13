package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserDefalutNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

	public FileUserService(UserRepository userRepository,
						   UserStatusRepository userStatusRepository,
						   BinaryContentRepository binaryContentRepository) {
		this.userRepository = userRepository;
		this.userStatusRepository = userStatusRepository;
		this.binaryContentRepository = binaryContentRepository;
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {
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

		UserStatus userStatus = new UserStatus(user.getId());
		userStatusRepository.save(userStatus);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse getUserById(GetUserByIdRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse getUserByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		return UserResponse.success(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream()
			.map(UserResponse::success)
			.toList();
	}

	@Override
	public UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		if (!user.getPassword().equals(request.getCurrentPassword())) {
			throw new InvalidPasswordException();
		}

		user.updatePassword(request.getNewPassword());
		userRepository.save(user);

		return new UpdateUserPasswordResponse(true);
	}

	@Override
	public UserResponse updateUserDefalutNickname(UpdateUserDefalutNicknameRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		user.updateDefaultNickname(request.getNickname());
		userRepository.save(user);

		return UserResponse.success(user);
	}

	@Override
	public UserResponse updateUserProfile(UpdateUserProfileImageRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		UUID oldProfileId = user.getProfileId();

		if (request.getUserProfileImage() != null) {
			// 새로운 프로필 이미지 제공 - 업데이트 (기존과 같아도 새로 저장)
			BinaryContent newProfileImage = request.getUserProfileImage().toBinaryContent();
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
	public DeleteUserResponse delete(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserStatusNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());

		return DeleteUserResponse.success(user);
	}

	@Override
	public DeleteUserResponse delete(String loginId) {
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserStatusNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());

		return DeleteUserResponse.success(user);
	}
}