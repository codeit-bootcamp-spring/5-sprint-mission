package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    UserRepository repo;

    public BasicUserService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public void create(User user) {
        repo.save(user);
    }

    @Override
    public void update(User user) {
        repo.delete(user);
        repo.save(user);
    }

    @Override
    public void delete(User user) {
        repo.delete(user);
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public User searchById(UUID id) {
        return repo.searchById(id);
    }

    @Override
    public List<User> searchByName(String name) {
        return repo.searchByName(name);
    }

    @Override
    public List<User> searchAll() {
        return repo.searchAll();
    }
}
