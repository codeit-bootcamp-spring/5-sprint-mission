package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final Path directory = Path.of("User");

    public FileUserRepository() throws IOException {
        try {
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException e) {
            System.out.println(directory + " Directory already exists!");
        }
    }

    @Override
    public User save(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("User/" + user.getId().toString()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("User/" + id.toString()))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        } catch (FileNotFoundException e) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        File[] files = directory.toFile().listFiles();

        for (File file : files) {
            if (file.isFile()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()))) {
                    User user = (User) ois.readObject();
                    users.add(user);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return users;
    }

    @Override
    public User update(UUID id, User user) {
        User u;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("User/" + id.toString()))) {
            u = (User) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setAge(user.getAge());

        save(u);
        return user;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Path.of("User/" + id.toString());
        if (Files.exists(path)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        Path path = Path.of("User/" + id.toString());
        try {
            Files.delete(path);
            System.out.println("삭제 성공");
        } catch (NoSuchFileException e) {
            System.out.println("삭제 실패");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
