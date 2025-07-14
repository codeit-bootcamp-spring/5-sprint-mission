package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성
    User createUser(UUID id, String username, String password, int age, String email);

    // 읽기 & 모두 읽기
    User readUser(UUID id);
    List<User> readAllUsers();

    // 수정
    User updateUsername(UUID id, String username);
    User updatePassword(UUID id, String password);
    User updateEmail(UUID id, String email);

    // 삭제
    boolean deleteUser(UUID id);


}
