package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<String, User> data;

    public JCFUserService(Map<String, User> data) {
        this.data = data;
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        Long currentTime = System.currentTimeMillis();
        user.setCreateAt(currentTime);
        user.setUpdateAt(currentTime);

        data.put(user.getId().toString(), user);
        return user;
    }

    @Override
    public User getUserById(UUID id) {
        return data.get(id.toString());
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || !data.containsKey(user.getId().toString())) {
            return null;
        }

        User existingUser = data.get(user.getId().toString());
        user.setCreateAt(existingUser.getCreateAt());
        user.setUpdateAt(System.currentTimeMillis());

        data.put(user.getId().toString(), user);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id.toString());
    }
}
