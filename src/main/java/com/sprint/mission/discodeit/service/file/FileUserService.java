package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository repository;

    public FileUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(User user) {
        repository.save(user);
    }

    @Override
    public User read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> readAll() {
        return repository.findAll();
    }

    @Override
    public void update(UUID id, String newName) {
        repository.update(id, newName);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}

