package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    final Map<UUID, User> data = new HashMap<>();

    private static JCFUserService instance;

    public JCFUserService() {}

    public static JCFUserService getInstance() {
        if (instance == null) {
            instance = new JCFUserService();
        }
        return instance;
    }

    @Override
    public User create(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("유저이름 또는 패스워드가 비어있습니다.");
        }
        User user = new User(username, password);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User find(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을수 없습니다.");
        }
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("목록이 비어 있습니다.");
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID userId, String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new NoSuchElementException("유저이름 또는 패스워드가 틀리거나 존재하지 않습니다.");
        }
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을수 없습니다.");
        }
        User user = data.get(userId);
        user.update(username, password);
        return user;
    }

    @Override
    public User delete(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("해당 유저를 찾을수 없습니다.");
        }
        User deletedUser = data.get(userId);
        data.remove(userId);
        return deletedUser;
    }
}
