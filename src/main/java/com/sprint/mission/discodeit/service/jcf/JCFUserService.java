package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    public JCFUserService(JCFUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String password, int age, String email) {
        User user = new User(username, password, age, email);
        userRepository.save(user);
        return user;
    }

    @Override
    public Optional<User> readUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        try {
            userRepository.update(user.getId(), user);
            System.out.println("수정 완료: " + user);
        } catch (NoSuchElementException e) {
            System.out.println("User not found");
        }
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            System.out.println("삭제 성공");
            userRepository.deleteById(id);
        } else {
            System.out.println("User not found");
        }
    }
}
