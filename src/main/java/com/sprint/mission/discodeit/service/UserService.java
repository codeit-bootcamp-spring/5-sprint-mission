package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    User createUser(String username); // 생성
    Optional<User> getUser(UUID userId); // 읽기
    List<User> getAllUsers(); // 모두 읽기
    User updateUser(UUID userId, User user); // 수정
    void deleteUser(UUID userId); // 삭제
    boolean existsById(UUID id); // 삭제 여부
}
