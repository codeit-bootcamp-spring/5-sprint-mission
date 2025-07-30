package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserRepository extends FileStore<User> implements UserRepository {

    private final Map<UUID, User> userMap = new HashMap<>();

    public FileUserRepository() {
        super("data/user.store");
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
        return new ArrayList<>(userMap.values());
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
                .filter(user -> user.getName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, String name) {
        User user = userMap.get(id);
        if (user != null) {
            user.updateName(name);
            saveToFile(userMap);
        }
        return user;
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
