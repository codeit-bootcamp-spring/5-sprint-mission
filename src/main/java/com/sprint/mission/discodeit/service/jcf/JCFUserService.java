package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;
import com.sprint.mission.discodeit.respository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.*;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final UserRepository userRepository =  new JCFUserRepository();

    // 생성
    @Override
    public User create(String name, String email, String password) {
        // 중복검사
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(String.format("이미 사용 중인 이메일입니다: %s", email));
        }

        User user = new User(name, email, password);
        return userRepository.save(user);
    }

    //조회
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

    // 수정
    @Override
    public User update(UUID id, String name) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다");
        }
        user.updateName(name);
        return userRepository.save(user); // 변경된 사용자 저장
    }

    // 삭제
    @Override
    public boolean delete(UUID id) {
        return userRepository.delete(id);
    }
}
