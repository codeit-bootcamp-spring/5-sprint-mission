package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    // FileUserRepository의 클래스 생성자
    // 저장 디렉토리 설정("USER")
    // 파일 확장자 설정 (".ser")
    // 디렉토리 생성 : USER 디렉토리가 존재하지 않으면 해당 디렉토리를 생성 / IOException 발생 시 RuntimeException을 발생시켜 알림
    public FileUserRepository() {
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()){
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // try-with-resources : FileOutputStream과 ObjectOutPutStream 을 생성
    // FileOutputStream : path가 가리키는 파일에 데이터를 쓰기 위한 파일 출력 스트림을 생성
    // ObjectOutputStream : FileOutputStream 위에 래핑되어, 객체를 바이트 스트림으로 직렬화하여 쓸 수 있게 해주는 스트림
    @Override
    public User save(User user) {
        Path path = Paths.get(DIRECTORY, user.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos  = new ObjectOutputStream(fos);) {
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
       try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois  = new ObjectInputStream(fis);) {
           user = (User)ois.readObject();
       } catch (FileNotFoundException e) {
           // 파일이 존재하지 않을 때 -> 사용자를 찾지 못한 것이므로 Optional.empty() 반환
           System.err.println("User file not found for ID: " + user.getId() + ".Details: " + e.getMessage());
           // 결과적으로 사용자를 찾지 못했기 때문에 비어있는 Optional 로 반환해야 "사용자를 찾을 수 없음" 표기할 수 있음
           return Optional.empty();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
       // return Optional.empty(); 을 사용하지 않는 이유.
        // Optional.empty()는 항상 비어있는 Optional만 반환함
        // 즉, 파일을 성공적으로 읽어와 user 객체가 할당되었더라도, 메서드는 무조건 비어있는 Optional을 반환하게 됨
        // 결론: 실제 사용자를 찾았음에도 불구하고 "사용자를 찾을 수 없음"을 의미하는 결과가 되어버려 로직 오류가 생김
       return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
