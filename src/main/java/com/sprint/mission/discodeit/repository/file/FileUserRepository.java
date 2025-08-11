package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FileUserRepository extends FileStore<User> implements UserRepository {

    private final Map<UUID, User> userMap = new HashMap<>();

    public FileUserRepository(String rootDir) {
        super(rootDir + "user.ser");
        Map<UUID, User> loaded = loadFromFile();
        if (loaded != null) {
            userMap.putAll(loaded);
        }
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        saveToFile(userMap);
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(userMap.values());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findByName(String name) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(name))
                .collect(Collectors.toList());
    }



    @Override
    public boolean delete(UUID id) {
        if (userMap.containsKey(id)) {
            userMap.remove(id);
            saveToFile(userMap);
            return true;
        } else {
            return false;
        }
    }
}
