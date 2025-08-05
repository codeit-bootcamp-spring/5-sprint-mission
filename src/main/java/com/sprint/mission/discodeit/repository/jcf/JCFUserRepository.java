package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (data.containsKey(id)) {
            return Optional.of(data.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public User delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        return data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
