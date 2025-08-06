package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(FileUserRepository userRepository) {
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
        if (userRepository.existsById(id)) {
            System.out.println("조회 성공: " + userRepository.findById(id).get());
            return userRepository.findById(id);
        }
        System.out.println("등록된 회원이 없습니다.");
        return Optional.empty();
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            System.out.println("수정 완료: " + user);
            return userRepository.update(user.getId(), user);
        } else {
            System.out.println("수정 실패");
            return null;
        }
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
