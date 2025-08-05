package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public User findByName(String name) {
        if (name == null || name.isBlank()) return null;
        for (User user : data.values()) {
            if (user.getName().equals(name)) return user;
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(User user) {
        save(user); // update는 save로 처리 가능
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
