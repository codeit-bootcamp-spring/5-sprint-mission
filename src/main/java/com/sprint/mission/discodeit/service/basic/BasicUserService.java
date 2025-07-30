package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
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
    public User create(User user) {
        return repo.save(user);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = searchById(id);
        user.updateName(name);
        return repo.save(user);
    }

    @Override
    public User addChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.addChannel(channel);
        return repo.save(user);
    }

    @Override
    public User deleteChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.deleteChannel(channel);
        return repo.save(user);
    }

    @Override
    public User delete(UUID id) {
        return repo.delete(id).orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public User searchById(UUID id) {
        return repo.searchById(id).orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> searchByName(String name) {
        if (repo.searchByName(name).isEmpty()) {
            throw new NoSuchElementException("해당하는 유저를 찾을 수 없습니다.");
        }
        return repo.searchByName(name);
    }

    @Override
    public List<User> searchAll() {
        return repo.searchAll();
    }
}
