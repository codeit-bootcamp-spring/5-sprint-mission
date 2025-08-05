package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String password) {
        User user = new User(username, password);
        return userRepository.save(user);
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String username, String password) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchElementException("User with id " + userId + " not found"));
        user.update(username, password);
        return userRepository.save(user);
    }

    @Override
    public User delete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchElementException("User with id " + userId + " not found"));
        return  userRepository.delete(userId);
    }
}
