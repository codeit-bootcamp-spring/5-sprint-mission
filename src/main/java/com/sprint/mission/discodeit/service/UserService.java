package com.sprint.mission.discodeit.service;

import java.util.List;

import com.sprint.mission.discodeit.dto.request.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.DeleteUserByLoingIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByIdRequest;
import com.sprint.mission.discodeit.dto.request.user.GetUserByLoginIdRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserPasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UpdateUserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.user.CreateUserResponse;
import com.sprint.mission.discodeit.dto.response.user.DeleteUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserPasswordResponse;
import com.sprint.mission.discodeit.dto.response.user.GetUserResponse;
import com.sprint.mission.discodeit.dto.response.user.UpdateUserResponse;

public interface UserService {
	// 생성
	CreateUserResponse createUser(CreateUserRequest request);

	// 읽기
	GetUserResponse getUserById(GetUserByIdRequest request);
	GetUserResponse getUserByLoginId(GetUserByLoginIdRequest request);
	List<GetUserResponse> getAllUsers();
	
	// 수정
	UpdateUserPasswordResponse updateUserPassword(UpdateUserPasswordRequest request);
	UpdateUserResponse updateUserProfile(UpdateUserProfileImageRequest request);
	
	// 삭제
	DeleteUserResponse deleteUser(DeleteUserByIdRequest request);
	DeleteUserResponse deleteUser(DeleteUserByLoingIdRequest request);
}
