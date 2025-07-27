package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private String DIRECTORY;
    private String EXTENSION;

    public FileUserService() {
        this.DIRECTORY = "USER/UserService";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User createUser(String email, String username, String password, String discriminator, UserStatus status) throws IllegalArgumentException {
        checkValidate(email, username, password, discriminator, status);
        User user = new User(email, username, password, discriminator, status);
        Path path = Paths.get(DIRECTORY, user.getId() + EXTENSION);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(user);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findById(UUID userId) {
        User user = null;
        Path path = Paths.get(DIRECTORY, userId +  EXTENSION);

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            user = (User) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
       List<User> users = new ArrayList<>();
       File dir = new File(DIRECTORY);
       File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENSION));
       if(files != null) {
           for(File file : files) {
               try (FileInputStream fis = new FileInputStream(file);
               ObjectInputStream ois = new ObjectInputStream(fis);) {
                   User user = (User) ois.readObject();
                   users.add(user);
               } catch (Exception e) {
                   e.printStackTrace();
                   throw new RuntimeException(e);
               }
           }
       }
       return users;
    }

    @Override
    public User update(UUID userId, String email, String username, String password, String discriminator, UserStatus status) {
        Path path = Paths.get(DIRECTORY, userId +  EXTENSION);
        User user= null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(path.toFile().exists()) {
                user = (User) ois.readObject();
                user.update(email, username, password, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(path.toFile(), false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public User deleteById(UUID userId) {
        Path path = Paths.get(DIRECTORY, userId +  EXTENSION);
        User user= null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(path.toFile().exists()) {
                user = (User) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void checkValidate(String email, String username, String password, String discriminator, UserStatus status) {
        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is null or blank.");
        } if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is null or blank.");
        } if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is null or blank.");
        } if(discriminator == null || discriminator.isBlank()) {
            throw new IllegalArgumentException("discriminator is null or blank.");
        } if(status == null) {
            throw new IllegalArgumentException("status is null.");
        }
    }
}
