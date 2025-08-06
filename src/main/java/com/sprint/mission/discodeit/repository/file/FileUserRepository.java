package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {

    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "users");

    public FileUserRepository() {
        createDirectory();
    }

    private void createDirectory() {
        try{
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패 : " + directory, e);
        }
    }

    private Path getFilePath(UUID id) {
        return directory.resolve(id.toString().concat(".ser"));
    }

    @Override
    public User save(User user) {
        Path file = getFilePath(user.getId());

        try (FileOutputStream fos = new FileOutputStream(file.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(user);
            return user;

        } catch (IOException e) {
            throw new RuntimeException("사용자 저장 실패 : " + file, e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path file = getFilePath(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }

        try (FileInputStream fis = new FileInputStream(file.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            User user = (User) ois.readObject();
            if (user != null) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("사용자 불러오기 실패: " + file, e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(directory).filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("사용자 로딩 실패: " + path, e);
                        }
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public User delete(UUID id) {
        Optional<User> optionalUser = findById(id);
        if (optionalUser.isEmpty()) return null;
        try {
            Files.deleteIfExists(getFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("사용자 삭제 실패: " + id, e);
        }
        return optionalUser.get();
    }
}
