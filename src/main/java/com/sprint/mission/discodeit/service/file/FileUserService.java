package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository repository;

    public FileUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저가 null입니다.");
        }
        if (repository.findById(user.getId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        }
        repository.save(user);
    }

    @Override
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 유저 ID가 null입니다.");
        }
        User original = repository.findById(id);
        if (original == null) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
        return new User(original); // 복사본 반환
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("수정할 user가 null입니다.");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("수정할 user의 ID가 null입니다.");
        }
        if (repository.findById(user.getId()) == null) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
        }
        repository.update(user);
    }

    @Override
    public void delete(UUID id) {
        // 🔒 ID가 null이면 예외
        if (id == null) {
            throw new IllegalArgumentException("삭제할 유저 ID가 null입니다.");
        }
        if (repository.findById(id) == null) {
            throw new IllegalStateException("삭제할 유저가 존재하지 않습니다: " + id);
        }
        repository.delete(id);
    }
}
