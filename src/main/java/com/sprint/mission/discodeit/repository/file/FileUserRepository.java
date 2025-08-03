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

public class FileUserRepository implements UserRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileUserRepository(){
        DIRECTORY ="USER";
        EXTENSION =".ser";
        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public User save(User user) {
        Path path = new File(DIRECTORY,user.getId()+EXTENSION).toPath();
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = null;
        Path path = Paths.get(DIRECTORY, id + EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);) {
            user=(User)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        File file = new File(DIRECTORY);
        File[] folder = file.listFiles((dir, name)->name.endsWith(EXTENSION));

        if(folder==null){return userList;}

        for(File f:folder){
            try (FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
                userList.add((User)ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userList;

    }

    @Override
    public long count() {
        File folder =  new File(DIRECTORY);
        File[] files = folder.listFiles((dir,name)->name.endsWith(EXTENSION));
        if(files==null) return 0;
        ;
        return files.length;
    }

    @Override
    public boolean delete(UUID id) {
        File file =new File(DIRECTORY, id + EXTENSION);
        return file.delete();
    }

    @Override
    public boolean existsById(UUID id) {
        File file = new File(DIRECTORY, id + EXTENSION);
        return file.isFile();
    }

    @Override
    public boolean update(UUID UserId, String username, String password) {
        File file = new File(DIRECTORY, UserId + EXTENSION);
        User user;
        try (FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            user = (User)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if(user.getUsername().equals(username) && user.getPassword().equals(password)){
            System.out.println("수정 전과 일치합니다.");
            return false;
        }else {
            user.update(username, password);
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos);){
            oos.writeObject(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
