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
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
