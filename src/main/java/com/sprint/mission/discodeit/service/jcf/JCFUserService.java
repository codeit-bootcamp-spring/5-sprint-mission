package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    public User create(String nickname) {
        User user = new User(nickname);
        data.put(user.getId(), user);
        return user;
    }

    public User findById(UUID id) {
        return data.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String newNickname) {
        User user = data.get(id);
        if (user != null) user.updateNickname(newNickname);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}
