package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private static final JCFUserService instance = new JCFUserService();

    private JCFUserService() {
        this.data = new HashMap<>();
    }

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void create(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User get(UUID id) {
        return data.get(id);
    }


    @Override
    public User get(String name) {
        if (name == null || name.isBlank()) return null;

        for (User user : data.values()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(User user) {
        if(data.containsKey(user.getId())) {
            data.put(user.getId(), user);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
