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
        return new ArrayList<>(data.values());
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User update(UUID id, String name) {
        User user = data.get(id);
        if (user != null) {
            user.updateName(name); // 혹은 user.setName(name); 네 메서드 이름에 맞게
        }
        return user;
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
