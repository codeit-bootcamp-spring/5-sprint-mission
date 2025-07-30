package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    // 저장 방식(JCF 또는 File)에 따라 적절한 구현체를 주입받아 사용
    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String password) {
        // 중복검사
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(String.format("이미 사용 중인 이메일입니다: %s", email));
        }

        User user = new User(name, email, password);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name) {
        return userRepository.update(id, name);
    }

    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}
