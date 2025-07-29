package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.w3c.dom.ls.LSOutput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService() {
        userRepository = new FileUserRepository();
    }

    @Override
    public User createUser(String email, String username, String password, String discriminator, UserStatus status) {
        checkValidate(email, username, password, discriminator, status);

        return userRepository.save(new User(email, username, password, discriminator, status));
    }

    @Override
    public User findById(UUID userId) {
        User findUser =  userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("[Error] : id{" + userId + "}는 존재하지 않는 사용자입니다."));

        return findUser;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String email, String username, String password, String discriminator, UserStatus status) {
        checkValidate(email, username, password, discriminator, status);

        User existUser = findById(userId);
        existUser.update(email, username, password, status);

        return userRepository.save(existUser);
    }

    @Override
    public User deleteById(UUID userId) {
        return userRepository.delete(userId);
    }

    @Override
    public void checkValidate(String email, String username, String password, String discriminator, UserStatus status) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is null or blank.");
        } if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is null or blank.");
        } if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is null or blank.");
        } if(discriminator == null || discriminator.isBlank()) {
            throw new IllegalArgumentException("discriminator is null or blank.");
        } if(status == null) {
            throw new IllegalArgumentException("status is null.");
        }
    }
}
