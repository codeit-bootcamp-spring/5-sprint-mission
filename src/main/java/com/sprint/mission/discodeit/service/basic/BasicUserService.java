package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {

        if (user == null) {
            return null;
        }

        if (user.getName() == null || user.getEmail() == null || user.getpassword() == null) {
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public User create(String name, String email, String password) {

        if (name == null || email == null || password == null) {
            return null;
        }

        return userRepository.save(new User(name, email, password, true));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(UUID id, String name, boolean isOnline) {
        User user = userRepository.findById(id);

        if (user == null) {
            return null;
        }

        user.update(name, isOnline);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);

        if (user != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
