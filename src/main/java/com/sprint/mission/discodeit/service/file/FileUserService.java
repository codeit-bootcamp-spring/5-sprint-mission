package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileUserRepository userRepository = new FileUserRepository();

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
        existing.updateTimestamp(); // 필드 수정은 요구사항에 없으므로 생략
        return userRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
