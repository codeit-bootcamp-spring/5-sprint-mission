package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public User create(String userId, String name, String password) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("사용자 ID를 입력해주세요.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
            }
        }

        User user = new User(userId, password, name);
        data.add(user);
        return user;
    }

    @Override
    public User get(String userId) {
        return data.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User updateUserName(String userId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }

        User user = get(userId);
        user.setName(name);
        return user;
    }

    @Override
    public User updatePassword(String userId, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("기존 비밀번호와 새 비밀번호를 모두 입력해주세요.");
        }
        User user = get(userId);
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        user.setPassword(newPassword);
        return user;

    }

    @Override
    public boolean delete(String userId) {
        User user = get(userId);
        return data.remove(user);
    }
}
