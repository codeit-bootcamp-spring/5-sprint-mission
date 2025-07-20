package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.model.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<Long, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public void create(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User read(Long id) {
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Long id, User user) {
        if (data.containsKey(id)) {
            data.put(id, user);
        }
    }

    @Override
    public void delete(Long id) {
        data.remove(id);
    }
}