package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository userRepository = new FileUserRepository();

    @Override
    public User createUser(String username, String password, String nickname) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        User user = new User(username, password, nickname);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUser(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID userId, String username, String password, String nickname) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        return userRepository.update(user.update(username, password, nickname));
    }

    @Override
    public User deleteUser(UUID userId) {
        return userRepository.delete(userId);
    }
}
