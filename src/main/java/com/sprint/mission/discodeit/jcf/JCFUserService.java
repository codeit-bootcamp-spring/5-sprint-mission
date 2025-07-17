package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> users = new ArrayList<>();

    @Override
    public User register(User user) {
        if (user.getName() == null || user.getPassword() == null || user.getName().isBlank() || user.getPassword().isBlank()) {
            System.out.println("사용자 등록 실패");
            return null;
        }
        users.add(user);
        System.out.println("사용자 : " + user.getName() + " 등록 성공");
        return user;
    }

    @Override
    public User findById(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public User update(UUID id, String newPW) {
        if (newPW != null && !newPW.isBlank()) {
            for (User user : users) {
                if (user.getId().equals(id)) {
                    user.setPassword(newPW);
                    user.setUpdateAt(System.currentTimeMillis());
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public User delete(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                users.remove(user);
                return user;
            }
        }
        return null;
    }
}
