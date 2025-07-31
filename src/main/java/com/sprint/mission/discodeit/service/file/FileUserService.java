package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private final FileUserRepository fileRepo;

    public FileUserService(FileUserRepository fileUserRepository) {
        this.fileRepo = fileUserRepository;
    }

    @Override
    public User createUser(String username) {
        User user = new User(username);
        return fileRepo.save(user);
    }

    @Override
    public Optional<User> getUser(UUID userId) {
        Optional<User> user = fileRepo.findById(userId);
        if(user.isEmpty()){
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        return fileRepo.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return fileRepo.findAll();
    }

    @Override
    public User updateUser(UUID userId, User user) {
        return fileRepo.update(userId, user);
    }

    @Override
    public void deleteUser(UUID userId) {
        fileRepo.delete(userId);
    }

    @Override
    public boolean existsById(UUID id) {
        return fileRepo.existsById(id);
    }
}
