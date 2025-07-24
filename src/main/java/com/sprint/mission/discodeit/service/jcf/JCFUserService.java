package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 생성
    public User create(String name, String password) {
        User user = new User(name, password);
        return userRepository.save(user);
    }

    //조회
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 수정
    public User update(UUID id, String name) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다");
        }
        user.updateName(name);
        return userRepository.save(user); // 변경된 사용자 저장
    }

    // 삭제
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
