package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

    private Map<UUID, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, User user) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException();
        }
        users.put(id, user);
        return user;
    }

    @Override
    public boolean existsById(UUID id) {
        if (users.containsKey(id)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        users.remove(id);
    }
}
