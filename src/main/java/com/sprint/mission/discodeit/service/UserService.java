package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateDefaultNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
	// 생성
	UserResponse createUser(UserCreateRequest request);

	// 읽기
	UserResponse findById(UUID userId);
	UserResponse findByUsername(String username);
	List<UserResponse> findAll();

	// 수정
	UserResponse update(UUID id, UserUpdateRequest request, UserProfileImageRequest profileImageRequest);
	UserResponse updateUserPassword(UUID id, UserUpdatePasswordRequest request);
	UserResponse updateUserProfile(UUID id, UserProfileImageRequest request);
	UserResponse updateUserDefalutNickname(UUID userId, UserUpdateDefaultNicknameRequest request);

	// 삭제
	UserDeleteResponse delete(UUID id);
	UserDeleteResponse delete(String loginId);
}
