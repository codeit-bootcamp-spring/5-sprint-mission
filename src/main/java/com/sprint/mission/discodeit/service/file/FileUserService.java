package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository userRepository;

    public FileUserService() {
        userRepository = new FileUserRepository();
    }

    @Override
    public boolean register(String email, String userName, String nickname, String password, String phoneNumber) {
        if (userRepository.findByEmail(email) != null) return false;
        if (userRepository.findByUserName(userName) != null) return false;

        userRepository.save(new User(UUID.randomUUID(), Instant.now().getEpochSecond(), email, userName, nickname, password, phoneNumber));
        return true;
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> searchByNickname(String nickname) {
        return userRepository.findByNickName(nickname);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean updateByEmail(String email, String userName, String nickname, String password, String phoneNumber) {
        if (userRepository.findByUserName(userName) != null) return false;
        return userRepository.updateByEmail(email, userName, nickname, password, phoneNumber);
    }

    @Override
    public boolean updateByUserName(String userName, String email, String nickname, String password, String phoneNumber) {
        if (userRepository.findByEmail(email) != null) return false;
        return userRepository.updateByUserName(userName, email, nickname, password, phoneNumber);
    }

    @Override
    public boolean removeByEmail(String email) {
        return userRepository.deleteByEmail(email);
    }

    @Override
    public boolean removeByUserName(String userName) {
        return userRepository.deleteByUserName(userName);
    }
}
