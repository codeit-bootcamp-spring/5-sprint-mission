package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final UserRepository repository;

    public JCFUserService(UserRepository repository) {
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
    public boolean update(UUID id, String newName) {
        return repository.update(id, newName);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }

    // ✅ 추가 구현
    @Override
    public User create(String name, String email, String password) {
        User user = new User(name, email, password); // 해당 생성자 필요
        repository.save(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(repository.findById(id));
    }
}


