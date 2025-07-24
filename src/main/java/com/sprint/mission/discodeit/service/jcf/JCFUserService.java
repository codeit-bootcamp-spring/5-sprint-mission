package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> userList;

    public JCFUserService() {
        userList = new ArrayList<>();
    }

    // 사용자 추가
    @Override
    public User create(String name, String email, String password) {
        if (name == null || email == null || password == null || name.isBlank() || email.isBlank() || password.isBlank()) {
            return null;
        }
        User user = new User(name, email, password);
        userList.add(user);
        return user;
    }

    // 사용자 조회
    @Override
    public User find(UUID userId) {
        for (User user : userList) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // 사용자 전체 조회
    @Override
    public List<User> findAll() {
        return userList;
    }

    // 사용자 수정
    @Override
    public User update(UUID userId, String name, String email, String password) {
        for (User user : userList) {
            if (user.getId().equals(userId)) {
                user.update(name, email, password);
                return user;
            }
        }
        return null;
    }


    // 사용자 삭제
    @Override
    public boolean delete(UUID userId) {
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user.getId().equals(userId)) {
                userList.remove(i);
                return true;
            }
        }
        return false;
    }
}
