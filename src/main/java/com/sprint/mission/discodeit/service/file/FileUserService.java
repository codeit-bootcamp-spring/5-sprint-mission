package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.respository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;

public class FileUserService implements UserService {

    private final UserRepository userRepository = new FileUserRepository();

    @Override
    public User create(String name, String password) {

        //중복검사
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getName().equals(name)) {
                throw new IllegalArgumentException("이미 사용중인 이름입니다.");
            }
        }

        User user = new User(name, password);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userRepository.findById(id));
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
