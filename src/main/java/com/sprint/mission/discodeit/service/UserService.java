package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
	//
	User login(String loginId, String password);

	// 생성
	User createUser(UserCreateRequest request);

	// 읽기
	User getUserById(UUID id);
	User getUserByLoginId(String loginId);
	List<User> getAllUsers();
	
	// 수정
	boolean updateUserPassword(UUID id, String password);
	
	// 삭제
	boolean deleteUser(UUID id);
	boolean deleteUser(String LoginId);
}
