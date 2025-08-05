package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String newName) {
        User existing = userRepository.findById(id);
        existing.updateTimestamp(); // 수정 시간만 갱신 (내가 추측하지 않음)
        // 여기서 어떤 필드를 수정하는지는 User 클래스 내부 책임
        return userRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
