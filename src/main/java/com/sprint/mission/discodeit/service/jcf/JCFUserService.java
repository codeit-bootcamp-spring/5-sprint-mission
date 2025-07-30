package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        if (isNotValid(user)) {
            throw new IllegalArgumentException("invalid user info");
        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("user not found");
        }

        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public User update(UUID id, User userDto) {
        User user = data.get(id);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }

        if (isNotValid(userDto)) {
            throw new IllegalArgumentException("invalid user info");
        }

        user.update(userDto.getName(), userDto.getNickname(), userDto.getPassword());

        return user;
    }

    @Override
    public void delete(UUID id) {
        User removed = data.remove(id);
        removed.withdraw();
    }

    private boolean isNotValid(User user) {
        return user == null || user.getName() == null || user.getName().isBlank()
                || user.getNickname() == null || user.getNickname().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank();
    }
}
