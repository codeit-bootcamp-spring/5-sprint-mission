package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {

    UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = searchById(id);
        user.updateName(name);
        return userRepository.save(user);
    }

    @Override
    public User addChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.addChannel(channel);
        return userRepository.save(user);
    }

    @Override
    public User deleteChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.deleteChannel(channel);
        return userRepository.save(user);
    }

    @Override
    public User delete(UUID id) {
        return userRepository.delete(id).orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public User searchById(UUID id) {
        return userRepository.searchById(id).orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> searchByName(String name) {
        if (userRepository.searchByName(name).isEmpty()) {
            throw new NoSuchElementException("해당하는 유저를 찾을 수 없습니다.");
        }
        return userRepository.searchByName(name);
    }

    @Override
    public List<User> searchAll() {
        return userRepository.searchAll();
    }
}
