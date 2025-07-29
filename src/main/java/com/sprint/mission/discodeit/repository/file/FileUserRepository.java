package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
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
    private final List<User> data;




    public FileUserRepository() {
        this.data=new ArrayList<>();
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        Path path= Paths.get(DIRECTORY,user.getId()+EXTENSION);
        //File newFile=new File("/"+DIRECTORY+"/"+user.getId()+EXTENSION);
        try(FileOutputStream fos= new FileOutputStream(path.toFile());
            ObjectOutputStream oos =new ObjectOutputStream(fos)){
            oos.writeObject(user);
            data.add(user);

        }catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        User user= null;
        Path path= Paths.get(DIRECTORY,userId.toString()+EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis); ){
            user=(User)ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        File folder = new File(DIRECTORY);

        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    User user = (User) ois.readObject();
                    users.add(user);

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return users;
    }

    @Override
    public long count() {
        List<User> users = new ArrayList<>();
        File folder = new File(DIRECTORY);

        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    User user = (User) ois.readObject();
                    users.add(user);

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return users.size();

    }

    @Override
    public User delete(UUID userId) {
        Path path = Paths.get(DIRECTORY, userId.toString() + EXTENSION);
        File file = path.toFile();

        // 먼저 객체를 읽어옴
        User user = null;
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                user = (User) ois.readObject();
            } catch (Exception e) {
                throw new RuntimeException("삭제 전 사용자 로딩 실패: " + e.getMessage(), e);
            }
            // 그 다음 파일 삭제
            if (!file.delete()) {
                throw new RuntimeException("파일 삭제 실패: " + file.getAbsolutePath());
            }
        } else {
            throw new RuntimeException("삭제할 파일이 존재하지 않습니다: " + file.getAbsolutePath());
        }
        data.remove(user);
        return user;
    }

    @Override
    public boolean existsById(UUID userId) {
        File folder = new File(DIRECTORY);
        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    User user = (User) ois.readObject();
                    if(user.getId().equals(userId)){
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
