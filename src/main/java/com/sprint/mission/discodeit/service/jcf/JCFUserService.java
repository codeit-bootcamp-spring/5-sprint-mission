package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public User create(User user) {

        if (user == null) {
            return null;
        }

        data.add(user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return data;
    }

    @Override
    public User get(UUID id) {
        return data.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public User update(UUID id, String name, boolean isOnline) {
        User user = data.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);

        if (user == null) {
            return null;
        }

        user.update(name, isOnline);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User target = data.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (target != null) {
            data.remove(target);
        }
    }
}
