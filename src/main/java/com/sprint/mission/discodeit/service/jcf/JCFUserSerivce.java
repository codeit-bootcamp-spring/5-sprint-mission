package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserSerivce implements UserService {

    UserRepository repo;

    public JCFUserSerivce(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User createUser(String username) {
        User user = new User(username);
        return repo.save(user);
    }

    @Override
    public Optional<User> getUser(UUID userId) {
        Optional<User> user = repo.findById(userId);
        if(user.isEmpty()){
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User updateUser(UUID userId, User user) {
        return repo.update(userId, user);
    }

    @Override
    public User deleteUser(UUID userId) {
       return repo.delete(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return repo.existsById(userId);
    }
}
