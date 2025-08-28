package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void createUser(String username, String password) {
        for (User u : users.values()) {
            if (u.getUsername().equals(username)) {
                System.out.println("이미 존재하는 사용자명입니다.");
                return;
            }
        }
        User user = new User(username, password);
        users.put(user.getId(), user);
        System.out.println("사용자가 생성되었습니다: " + user);
    }

    @Override
    public User findUserById(UUID id) {
        User user = users.get(id);
        if (user == null) {
            System.out.println("해당 ID의 사용자가 없습니다.");
        }
        return user;
    }

    @Override
    public User findUser(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) return user;
        }
        System.out.println("해당 사용자명이 존재하지 않습니다.");
        return null;
    }

    @Override
    public List<User> findAllUsers() {
        if (users.isEmpty()) {
            System.out.println("등록된 사용자가 없습니다.");
        }
        return new ArrayList<>(users.values());
    }

    @Override
    public void updateUser(UUID id, String newUsername, String newPassword) {
        User user = users.get(id);
        if (user == null) {
            System.out.println("수정할 사용자가 없습니다.");
            return;
        }
        user.update(newUsername, newPassword);
        System.out.println("사용자가 수정되었습니다: " + user);
    }

    @Override
    public void deleteUser(UUID id) {
        User removed = users.remove(id);
        if (removed == null) {
            System.out.println("삭제할 사용자가 없습니다.");
        } else {
            System.out.println("사용자가 삭제되었습니다: " + removed);
        }
    }
}
