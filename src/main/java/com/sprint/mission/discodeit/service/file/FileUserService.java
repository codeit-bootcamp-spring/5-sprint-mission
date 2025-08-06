package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("[!] id가 null 이거나 비어있을 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("[!] email이 null 이거나 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("[!] password가 null 이거나 비어있을 수 없습니다.");
        }

        if (existsByUsername(username)) {
            throw new IllegalArgumentException("[!] 중복된 userid가 있습니다.");
        }

        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    private boolean existsByUsername(String username) {
        return userRepository.findAll().stream().anyMatch(u -> (u.getUsername().equals(username)));
    }

    @Override
    public User find(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[!] 해당 user가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchByUsernameOrEmail(String token) {
        return findAll().stream().filter(
                u -> (u.getUsername().contains(token) || u.getEmail().contains(token))).toList();
    }

    @Override
    public User update(UUID id, UUID requestId, String newUsername, String newEmail, String newPassword) {
        User user = find(id);
        if (!id.equals(requestId)) {
            throw new IllegalArgumentException("[!] 수정 권한이 없습니다.");
        }
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id, UUID requestId) {
        if (!id.equals(requestId)) {
            throw new IllegalArgumentException("[!] 삭제 권한이 없습니다.");
        }
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("[!] 사용자가 존재하지 않습니다.");
        }
        userRepository.deleteById(id);
    }
}
