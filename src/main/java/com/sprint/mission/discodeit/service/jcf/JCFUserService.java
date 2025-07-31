package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap;

    public JCFUserService() {
        userMap = new HashMap<>();
    }

    // 사용자 추가
    @Override
    public User create(String name, String email, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("User info is invalid");
        }
        User user = new User(name, email, password);
        userMap.put(user.getId(), user);
        return user;
    }

    // 사용자 조회
    @Override
    public User find(UUID userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        return user;
    }

    // 사용자 전체 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    // 사용자 수정
    @Override
    public User update(UUID userId, String name, String email, String password) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        user.update(name, email, password);
        return user;
    }


    // 사용자 삭제
    @Override
    public void delete(UUID userId) {
        userMap.remove(userId);
    }
}
