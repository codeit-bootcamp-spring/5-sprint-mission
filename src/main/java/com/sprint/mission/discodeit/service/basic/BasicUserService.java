package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
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
        User u = repo.delete(user).orElse(null);
        if (u == null) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        repo.save(user);
    }

    @Override
    public void delete(User user) {
        User u = repo.delete(user).orElse(null);
        if (u == null) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public User searchById(UUID id) {
        User user = repo.searchById(id).orElse(null);
        if (user == null) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return user;
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
        if (repo.searchAll().isEmpty()) {
            System.out.println("등록 된 유저가 없습니다.");
        }
        return repo.searchAll();
    }
}
