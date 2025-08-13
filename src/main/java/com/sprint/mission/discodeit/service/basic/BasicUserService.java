package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.user.*;
import com.sprint.mission.discodeit.dto.response.user.*;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

	@Override
	public CreateUserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByLoginId(request.getLoginId())) {
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


		return CreateUserResponse.success(user);
	}

	@Override
	public GetUserResponse getUserById(GetUserByIdRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		return GetUserResponse.success(user);
	}

	@Override
	public GetUserResponse getUserByLoginId(GetUserByLoginIdRequest request) {
		User user = userRepository.findByLoginId(request.getLoginId())
			.orElseThrow(UserNotFoundException::new);

		return GetUserResponse.success(user);
	}


	@Override
	public List<GetUserResponse> getAllUsers() {
		return userRepository.findAll().stream()
			.map(GetUserResponse::success)
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

		// 만약 비밀번호 설정 제약이 있다면 받은 비밀번호 검사 후 Response 반환
		return new UpdateUserPasswordResponse(true);
	}

	@Override
	public UpdateUserResponse updateUserProfile(UpdateUserProfileImageRequest request) {
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

		return UpdateUserResponse.builder()
			.id(user.getId())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.email(user.getEmail())
			.defaultNickname(user.getDefaultNickname())
			.profileId(user.getProfileId())
			.success(true)
			.build();
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByIdRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());

		return new DeleteUserResponse(true);
	}

	@Override
	public DeleteUserResponse deleteUser(DeleteUserByLoingIdRequest request) {
		User user = userRepository.findByLoginId(request.getLoginId())
			.orElseThrow(UserNotFoundException::new);

		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(UserStatusNotFoundException::new);

		if(user.getProfileId() != null) {
			binaryContentRepository.deleteById(user.getProfileId());
		}
		userStatusRepository.deleteById(userStatus.getId());
		userRepository.deleteById(user.getId());

		return new DeleteUserResponse(true);
	}
}
