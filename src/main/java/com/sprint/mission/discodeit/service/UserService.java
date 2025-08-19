package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserDefaultNicknameRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;

public interface UserService {
	// 생성
	UserResponse createUser(CreateUserRequest request);

	// 읽기
	UserResponse getUserById(GetUserByIdRequest request);
	UserResponse getUserByLoginId(String loginId);
	List<UserResponse> getAllUsers();
	
	// 수정
	UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request);
	UserResponse updateUserProfile(UpdateUserProfileImageRequest request);
	UserResponse updateUserDefalutNickname(UpdateUserDefaultNicknameRequest request);
	
	// 삭제
	DeleteUserResponse delete(UUID id);
	DeleteUserResponse delete(String loginId);
}
