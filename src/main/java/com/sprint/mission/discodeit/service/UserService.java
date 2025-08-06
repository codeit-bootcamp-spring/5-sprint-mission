package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // 생성
    User createUser(String username, String password, int age, String email);

    // 읽기 & 모두 읽기
    Optional<User> readUser(UUID id);
    List<User> readAllUsers();

    // 수정
    User updateUser(User user);

    // 삭제
    void deleteUser(UUID id);


}
