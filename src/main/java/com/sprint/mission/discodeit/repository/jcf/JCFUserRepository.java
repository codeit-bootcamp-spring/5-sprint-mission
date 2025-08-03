package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User userDto) {
        data.put(userDto.getId(), userDto);
        return userDto;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public User update(UUID id, User userDto) {
        User user = data.get(id);
        user.update(userDto.getName(), userDto.getNickname(), userDto.getPassword());
        return user;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
