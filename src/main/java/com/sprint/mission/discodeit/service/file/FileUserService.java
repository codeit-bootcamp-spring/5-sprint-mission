package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {

    UserRepository repo;

    public FileUserService(UserRepository repo) {
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
        List<User> users = repo.searchByName(name);
        if (users.isEmpty()) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }

        return users;
    }

    @Override
    public List<User> searchAll() {
        return repo.searchAll();
    }
}
