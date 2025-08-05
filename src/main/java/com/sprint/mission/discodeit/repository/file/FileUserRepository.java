package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileUserRepository() {
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        Path path = Paths.get(DIRECTORY, user.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = null;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream oos = new ObjectInputStream(fis)) {
            user = (User)oos.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        Path DIRECTORY = Paths.get("USER");
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public User delete(UUID userId) {
        Path path = Paths.get(DIRECTORY, userId.toString() + EXTENSION);

        // Optional을 사용해 유저 조회
        Optional<User> optionalUser = findById(userId);

        // 유저가 존재하지 않으면 예외 던짐
        User user = optionalUser.orElseThrow(() -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));

        // 파일 삭제 시도
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("유저 삭제 실패", e);
        }

        return user;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        if (path.toFile().exists()) {
            return true;
        }
        return false;
    }
}
