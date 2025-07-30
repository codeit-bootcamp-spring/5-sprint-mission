package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.respository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class FileUserService implements UserService {

    private final UserRepository userRepository = new FileUserRepository();

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
        return userRepository.findById(id);
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
