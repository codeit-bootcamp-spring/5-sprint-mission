package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("[!] id가 null 이거나 비어있을 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("[!] email이 null 이거나 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("[!] password가 null 이거나 비어있을 수 없습니다.");
        }

        for (UUID id : data.keySet()) {
            if (data.get(id).getUsername().equals(username)) {
                throw new IllegalArgumentException("[!] 중복된 id가 있습니다.");
            }
        }
        User user = new User(username, email, password);
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public User find(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("[!] 사용자가 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("[!] 결과가 없습니다.");
        }
        return new ArrayList<>(data.values());
    }

    @Override
    public List<User> searchByUsernameOrEmail(String token) {
        return data.values().stream().filter(
                u -> (u.getUsername().contains(token) || u.getEmail().contains(token))).toList();
    }

    @Override
    public User update(UUID id, UUID requestId, String newUsername, String newEmail, String newPassword) {
        User user = data.get(id);
        if (!id.equals(requestId)) {
            throw new IllegalArgumentException("[!] 수정 권한이 없습니다.");
        }
        user.update(newUsername, newEmail, newPassword);
        return user;
    }

    @Override
    public void delete(UUID id, UUID requestId) {
        if (!id.equals(requestId)) {
            throw new IllegalArgumentException("[!] 삭제 권한이 없습니다.");
        }
        data.remove(id);
    }
}
