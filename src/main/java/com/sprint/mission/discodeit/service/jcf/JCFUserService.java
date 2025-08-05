package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final JCFUserRepository userRepository;

    public JCFUserService(JCFUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {

        if (user == null) {
            return null;
        }

        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public User create(String name, String email, String password) {

        if (name == null || email == null || password == null) {
            return null;
        }

        return userRepository.save(new User(name, email, password, null));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User update(UUID id, String name, UUID profileId) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        user.update(name, profileId);
        return user;
    }

    // TODO 추후 불필요 메서드 제거

    @Override
    public UserDto.DetailResponse create(UserDto.CreateRequest request) {
        return null;
    }

    @Override
    public UserDto.DetailResponse update(UserDto.UpdateRequest request) {
        return null;
    }

    @Override
    public UserDto.DetailResponse findById(UUID id) {
        return null;
    }

    @Override
    public List<UserDto.DetailResponse> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            userRepository.delete(id);
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
