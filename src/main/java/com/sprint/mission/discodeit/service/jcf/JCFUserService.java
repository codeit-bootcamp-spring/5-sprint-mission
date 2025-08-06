package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {
    private static JCFUserService instance;
    final Map<UUID, User> data = new HashMap<>();

    private JCFUserService() {
    }

    public static synchronized JCFUserService getInstance() {
      if (instance == null) {
        instance = new JCFUserService();
      }
      return instance;
    }

    @Override
    public User create(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username can't be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password can't be null or blank");
        }

      if (data.values().stream().anyMatch(user -> user.getUsername().equals(username))) {
        throw new IllegalArgumentException("User with username '" + username + "' already exists.");
      }

      User user = new User(username, password);
      data.put(user.getId(), user);
      return user;
    }

    @Override
    public User find(UUID userid) {
      if(!data.containsKey(userid)) {
        throw new NoSuchElementException("user not found");
      }
      return data.get(userid);
    }


    @Override
    public List<User> findAll() {
      return data.values().stream().collect(Collectors.toList());
    }


    @Override
    public User update(UUID id, String username, String password) {
      User user = find(id);
      user.update(username, password);
      return user;
    }

    @Override
    public void delete(UUID id) {
      if (!data.containsKey(id)) {
        throw new NoSuchElementException("user not found");
      }
      data.remove(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public void clear() {
      data.clear();
    }
}
