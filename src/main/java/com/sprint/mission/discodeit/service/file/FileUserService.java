package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path directory;

    public FileUserService() {
        this.directory = Paths.get(System.getProperty("user.dir"), "user_data");
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException e) {
                throw new RuntimeException("user_data 폴더 생성 실패",e);
            }
        }
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if (userId == null) {
            System.err.println("오류: findById에 실패. userId가 null입니다.");
            return Optional.empty();
        }
        Path filePath = this.directory.resolve(userId.toString()+ ".ser");
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toString()))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("오류 : User 역직렬화 실패 : (" +userId + ")" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        if (user == null) {
            System.out.println("오류: User 생성에 실패. user가 null입니다.");
            return null;
        }
        Path filePath = directory.resolve(user.getUserId().toString() + ".ser");
        if (Files.exists(filePath)) {
            System.err.println("오류: 이미 존재하는 User ID입니다." + user.getUserId());
            return null;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(user);

        } catch (IOException e) {
            throw new RuntimeException("User 파일 저장 실패: " + filePath + e);
        }
        System.out.println("user 생성 : " + user);
        return user;


    }

    @Override
    public List<User> findAll() {
        if (Files.exists(this.directory)) {
            return List.of();
        } try {
            List<User> list = new ArrayList<>();
            Files.list(directory).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    User user = (User) ois.readObject();
                    list.add(user);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("오류: User 파일 로딩 실패 (" + path.getFileName() + "): " + e.getMessage());
                }
            });
            return list;
        }  catch (IOException e) {
            throw new RuntimeException("user_data 폴더 목록 조회 실패", e);
        }
    }

    @Override
    public Optional<User> updateId(UUID userId, User updateUser) {
        if (userId == null || updateUser == null) {
            System.err.println("오류: updateId 실패. userId 또는 updatedUser가 null입니다.");
            return Optional.empty();
        }
        Path filePath = this.directory.resolve(userId.toString() + ".ser");
        if (Files.exists(filePath)) {
            return Optional.empty();
        } try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(updateUser);
        } catch (IOException e) {
            System.err.println("오류 : User 업데이트 실패: "  + filePath + " / "+ e.getMessage());
            return Optional.empty();
        }
        System.out.println("updateUser = " + updateUser);
        return Optional.of(updateUser);
    }

    @Override
    public boolean deleteById(UUID userId) {
        if (userId == null) {
            System.out.println("오류: deleteById 실패. userId가 null입니다.");
            return false;
        }
        Path filePath = this.directory.resolve(userId.toString() + ".ser");
        try {
            boolean deletedId = Files.deleteIfExists(filePath);
            if (deletedId) {
                System.out.println("userId 삭제 : " + userId);
            } else {
                System.out.println("userId 살제 실패 : " + userId + "파일이 존재 하지 않습니다.");
            }
            return deletedId;

        } catch (IOException e) {
            System.err.println("오류 : User 파일 삭제 실패: "  + filePath + " / "+ e.getMessage());
            return false;
        }
    }
}
