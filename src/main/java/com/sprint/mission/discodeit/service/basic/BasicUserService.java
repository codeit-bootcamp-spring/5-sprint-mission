package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void create(User user) {
        userRepository.save(user);
    }

    @Override
    public User create(String name, String email, String password) {
        User user = new User(name, email, password);
        userRepository.save(user);
        return user;
    }

    @Override
    public User read(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean update(UUID id, String newName) {
        return userRepository.update(id, newName);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}

