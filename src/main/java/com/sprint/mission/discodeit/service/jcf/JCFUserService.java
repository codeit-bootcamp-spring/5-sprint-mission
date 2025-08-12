package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, User updatedUser) {
        User original = data.get(id);
        if (original != null) {
            original.updateName(updatedUser.getName());
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
