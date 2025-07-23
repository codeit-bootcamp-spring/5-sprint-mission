package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileUserRepository userRepository;

    public FileUserService(FileUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {

        if (user == null) {
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(UUID id, String name, boolean isOnline) {
        User user = userRepository.findById(id);

        if (user == null) {
            return null;
        }

        user.update(name, isOnline);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);

        if (user != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
