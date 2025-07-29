package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository; // UserRepository 의존성 주입 // 생성자 주입

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Optional<User> registerUser(String name, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        // 1. 이메일 중복 검사
        if (existingUser.isPresent()) {
            System.out.println("Registration failed: Email : " + email);
            return Optional.empty();
        }

        // 2. 새로운 User 객체를 생성
        User newUser = new User(name, email, password);

        // 3. UserRepository를 통해 사용자 저장
        userRepository.save(newUser);
        System.out.println("User registered successfully" + newUser.getEmail()); // 성공적으로 등록됨
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> loginUser(String email, String password) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public boolean deleteUser(UUID id) {
        return false;
    }
}
