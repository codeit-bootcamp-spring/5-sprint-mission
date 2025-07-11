package com.sprint.mission.discodeit.service;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.ROLE;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
	// 생성
	boolean	createUser(UUID id, String password, String defaultNickname);
	
	// 읽기
	User getUser(UUID id);
	User[] getUserAll();
	
	// 수정
	boolean updateUserPassword(UUID id, String password);
	boolean updateUserDefaultNickname(UUID id, String nickname);
	boolean updateRole(UUID id, ROLE role);
	
	// 삭제
	boolean deleteUser(UUID id);
}
