package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }


    @Override
    public void create(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User find(UUID id) {
        return  data.get(id);
    }

    @Override
    public ArrayList<User> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, User user) {

        if (data.containsKey(id)){
            data.put(id, user);
        }
    }


    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
