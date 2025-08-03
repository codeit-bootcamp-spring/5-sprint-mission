package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        validate(user);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public User update(UUID id, User userDto) {
        validate(userDto);

        User user = Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        user.update(userDto.getName(), userDto.getNickname(), userDto.getPassword());

        return user;
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(data.remove(id))
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    private void validate(User userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new IllegalArgumentException("User name is required");
        }
        if (userDto.getNickname() == null || userDto.getNickname().isBlank()) {
            throw new IllegalArgumentException("User nickname is required");
        }
         if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("User password is required");
        }
    }
}
