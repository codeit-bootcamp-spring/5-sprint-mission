package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
