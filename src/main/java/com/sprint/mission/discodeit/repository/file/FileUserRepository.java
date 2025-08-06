package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileUserRepository() {
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()){
            try {
                Files.createDirectory(path);
            } catch (IOException e) { // 입출력 작업 중 발생하는 예외
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        Path path = Paths.get(DIRECTORY, user.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
           oos.writeObject(user); // 파일에 객체 직렬화하기
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = null;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        // 역직렬화
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            user = (User)ois.readObject();
        } catch (IOException | ClassNotFoundException e) { // ClassNotFoundException : 역직렬화 시 클래스를 찾을 수 없을 때 발생
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        Path directory = Paths.get(DIRECTORY);
        if(Files.exists(directory)){
            try {
                List<User> users = Files.list(directory)
                        .map(path -> {
                            try(
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
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
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public User update(UUID id, User user) {
        // 예전 유저 정보 가져오기 -> 그 유저 정보 수정 -> 수정된 유저 정보 저장
        User user1;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

        if(!Files.exists(path)){
            throw new NoSuchElementException("User with id " + id + " not found");
        }

        // 예전 유저 정보 읽어오기
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
             user1 = (User) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 유저 이름 수정
        user1.update(user.getUsername( ), user.getEmail(), user.getPassword());

        // 수정된 유저 정보 파일에 저장하기
       save(user1);
       return user1;
    }

    @Override
    public void delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

        if(!Files.exists(path)){
            throw new NoSuchElementException("User with id " + id + " not found");
        }

        // 파일 삭제
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        return Files.exists(path);
    }
}
