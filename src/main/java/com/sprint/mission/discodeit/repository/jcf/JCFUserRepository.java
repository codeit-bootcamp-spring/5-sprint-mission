package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID,User> data = new HashMap<>();
    @Override
    public void save(Map<UUID, User> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public Map<UUID, User> loadData() {
        return new HashMap<>(data);
    }

    @Override
    public void clear() {
        data.clear();
    }
}
