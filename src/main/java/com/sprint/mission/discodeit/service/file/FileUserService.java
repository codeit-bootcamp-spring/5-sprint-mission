package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileUserRepository userRepo;

    public FileUserService(FileUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepo.save(user);
    }

    @Override
    public User find(UUID userId) {
        return userRepo.find(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepo.find(userId);
        user.update(newUsername, newEmail, newPassword);
        return userRepo.save(user);
    }

    @Override
    public void delete(UUID messageId) {
        userRepo.delete(messageId);
    }
}
