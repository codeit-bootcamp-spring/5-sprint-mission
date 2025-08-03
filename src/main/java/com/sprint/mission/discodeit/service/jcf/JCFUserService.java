package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final UserRepository userRepository;

    public JCFUserService() {
        this.userRepository = new JCFUserRepository();
    }

    @Override
    public User save(User userDto) {
        validate(userDto);
        return userRepository.save(userDto);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, User userDto) {
        validate(userDto);
        User user = findById(id);
        user.update(userDto.getName(), userDto.getNickname(), userDto.getPassword());
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user.getId());
    }

    private void validate(User userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new IllegalArgumentException("User name is required");
        }
        if (userDto.getNickname() == null || userDto.getNickname().isBlank()) {
            throw new IllegalArgumentException("User nickname is required");
        }
         if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("User password is required");
        }
    }
}
