package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository userRepository = new FileUserRepository();

    @Override
    public User register(User user) {
        if (isInvalid(user.getName()) || isInvalid(user.getPassword()))
            throw new IllegalArgumentException("사용자 등록에 실패했습니다.");
        System.out.println("사용자 : " + user.getName() + " 등록 성공.");
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("사용자에서 해당 " + id + "를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String newPW) {
        if (isInvalid(newPW))
            throw new IllegalArgumentException("새로운 비밀번호를 입력하세요.");

        Path path = Path.of("USER").resolve(id + ".ser");
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            User user = (User) ois.readObject();
            user.setUpdatedAt(System.currentTimeMillis());
            user.setPassword(newPW);
            return userRepository.save(user);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User delete(UUID id) {
        return userRepository.delete(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

}
