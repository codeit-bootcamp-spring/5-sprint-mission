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
        // 1. 이메일 사용자 조회
        Optional<User> existingUser = userRepository.findByEmail(email);

        // 2. 사용자가 존재하고 비밀번호가 일치한지 확인
        if (existingUser.isPresent()) { // 이메일이 동일한지 체크
            User user = existingUser.get();
            if (user.getPassword().equals(password)) { // 비밀번호가 동일한지 체크
                System.out.println("User logged successfully" + user.getEmail());
                return Optional.of(user); // 성공
            } else {
                System.out.println("Login failed[Incorrect Password]" + email);
            }
        } else {
            System.out.println("Login failed[Incorrect Email]" + email);
        }
        return Optional.empty(); // 로그인 실패 -> 아무것도 반환 x

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
