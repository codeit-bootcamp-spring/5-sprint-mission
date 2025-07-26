package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String DIRECTORY;
    private final String EXTENTSION;

    public static void main(String[] args) {
        UserRepository ur = new FileUserRepository();

        User user1 = ur.save(new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE));
        User user2 = ur.save(new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE));
        User user3 = ur.save(new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE));
        User user4 = ur.save(new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND));
        User user5 = ur.save(new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE));

        System.out.println(user1.toString());
        System.out.println(user2.toString());
        System.out.println(user3.toString());
        System.out.println(user4.toString());
        System.out.println(user5.toString());

        System.out.println("find user3 : " + ur.findById(user1.getId()));

        System.out.println("All users: ");
        List<User> users = ur.findAll();
        users.forEach(System.out::println);

        System.out.println("The number of files : " + ur.count());

        User delUser4 = ur.delete(user4.getId());
        System.out.println("Deleted user4 : " +  delUser4.toString());

    }

    public FileUserRepository() {
        this.DIRECTORY = "USER/FileUsers";
        this.EXTENTSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        Path path = Paths.get(DIRECTORY, user.getId() + EXTENTSION);
        try(FileOutputStream fos = new FileOutputStream(path.toFile(), false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = null;
        Path path = Paths.get(DIRECTORY, id + EXTENTSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            user = (User) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENTSION));
        if (files != null) {
            for (File file : files) {
                try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(file))) {
                    User user = (User) oos.readObject();
                    users.add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return users;
    }

    @Override
    public long count() {
        File dir = new File(DIRECTORY);
        Long count = (long) dir.listFiles((d, name) -> name.endsWith(EXTENTSION)).length;

        if(count != null) {
            return count;
        }

        return -1L;
    }

    @Override
    public User delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENTSION);
        User user = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if (existsById(id)) {
                user =  (User) ois.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public boolean existsById(UUID id) {
        File file = new File(DIRECTORY, id + EXTENTSION);
        if (file.exists()) {
            return true;
        }
        return false;
    }
}

