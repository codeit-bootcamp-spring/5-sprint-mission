package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final Path directory;

    public Path getDirectory() {
        return directory;
    }

    public FileUserService(Path directory) {
        this.directory = directory;
        initPath(directory);
    }

    public void initPath(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());

        try (FileOutputStream fos = new FileOutputStream(userDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> load(Path directory) {
        if (Files.exists(directory)) {
            try {
                List<User> users = Files.list(directory)
                        .map(path -> {
                            try (FileInputStream fis = new FileInputStream(path.toFile());
                                 ObjectInputStream ois = new ObjectInputStream(fis);) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void createUser(User user) {
        if (!load(directory).contains(user)) {
            save(user);
        } else {
            System.out.println("이미 존재하는 유저입니다.");
        }
    }

    @Override
    public void updateUser(User user) { // 리팩터링 후보
        deleteUser(user);
        save(user);
    }

    @Override
    public void deleteUser(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());
        if (Files.exists(userDirectory)) {
            try {
                Files.delete(userDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("존재하지 않는 유저입니다.");
        }
    }

    @Override
    public User searchByIndex(int i) { // 삭제 예정 메서드
        return null;
    }

    @Override
    public User searchById(UUID id) {
        for (User user : load(directory)) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();

        for (User user : load(directory)) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> getAllUsers() {
        return load(directory);
    }


    public static void main(String[] args) {
        FileUserService service = new FileUserService(Path.of("/Users/apple/dev_source/5-sprint-mission/userDirectory/"));
        User user1 = new User("test10");
        service.save(user1);
        System.out.println(service.load(service.getDirectory()));
        service.updateUser(user1);
        System.out.println(service.load(service.getDirectory()));
        service.deleteUser(user1);
        System.out.println(service.load(service.getDirectory()));
    }
}
