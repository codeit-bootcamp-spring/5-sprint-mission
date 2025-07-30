package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    public void create(User user) {
        data.put(user.getId(), user);
    }

    public User read(UUID id) {
        return data.get(id);
    }

    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String username) {
        User user = data.get(id);
        if (user != null) user.update(username);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}
