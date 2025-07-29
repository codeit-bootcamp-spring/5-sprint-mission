package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
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

    // FileUserRepository의 클래스 생성자
    // 저장 디렉토리 설정("USER")
    // 파일 확장자 설정 (".ser")
    // 디렉토리 생성 : USER 디렉토리가 존재하지 않으면 해당 디렉토리를 생성 / Exception 발생 시 RuntimeException을 발생시켜 알림
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

    // 디렉토리가 존재하고, 해당 디렉토리가 맞는 검증
    // 디렉토리가 존재하거나, 존재하더라도 디렉토리가 아닌 경우를 확인하는 목적이기 때문에 || 활용
    private boolean isValidDirectory(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)){
            System.err.println("[Repo]User directory does not exist ro is not a directory: " + DIRECTORY);
            return false;
        }
        return true;
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
           System.err.println("[Repo]User file not found for ID: " + user.getId() + ".Details: " + e.getMessage());
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

    // UUID의 ID는 파일명으로 직접 매핑되지만, Email은 파일명에 직접적으로 포함되지 않기 때문에 findById와 다른 접근 방식이 필요
    // 가장 일반적인 방법은 USER 디렉토리의 모든 사용자 파일을 읽어와서 각 파일의 내용을 역직렬화한 후, User 객체의 Email 필드를 확인하는 방식
    @Override
    public Optional<User> findByEmail(String email) {
        Path path = Paths.get(DIRECTORY);  // USER와 같은 사용자 파일이 저장된 디렉토리


        if (!isValidDirectory(path)) {  // 디렉토리 유효성 검사
            return Optional.empty();
        }

        // DirectoryStream : 특정 디렉토리 내의 항목들을 스트림 방식으로 효율적으로 탐색할 수 있도록 해주는 인터페이스
        // Files.newDirectoryStream() :  DirectoryStream 인스턴스를 생성하며, 두 개의 인자를 받음 (Path, glob(문자열 패턴))
        // 빅오 표기법 O(N * M)
        // N -> 총 사용자(.ser)의 파일 개수
        // M -> User 객체를 역직렬화하는데 걸리는 시간
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*" + EXTENSION)) {
            // "USER" 디렉토리 내의 모든 .ser 파일 순회
            for (Path entry : stream) {
                // 각 파일을 읽어서 User 객체로 역직렬화
                try (FileInputStream fis = new FileInputStream(entry.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis);){
                    // user 변수는 각 파일에서 객체를 읽어오는 시도마다 새롭게 생성되는 지역 변수 -> try-catch 내에서만 유효
                    // 지역변수로 활용되기 때문에 user 변수가 null인 채 외부로 반환될 걱정이 없음
                    User user = (User) ois.readObject();

                    // 불러온 User 객체의 email과 찾는 email이 일치하는지 확인
                    if (user != null && user.getEmail() != null && user.getEmail().equals(email)) {
                        return Optional.of(user); // 일치하는 사용자를 찾으면 바로 반환
                    }
                } catch (ClassNotFoundException | IOException e) {
                    // 특정 파일이 손상되었거나 클래스 정의가 없는 경우
                    // 해당 파일만 건너뛰고 다음 파일로 계속 진행(로그 기록은 필수)
                    System.err.println("[Repo]Error reading user file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // 디렉토리 스트림 생성 중 오류 발생
            throw new RuntimeException("[Repo]Error accessing user directory: " + DIRECTORY, e);
        }
        return Optional.empty();  // 모든 파일을 확인했지만 일치하는 이름을 찾지 못함
    }

    @Override
    public Optional<User> findByName(String name) {
        Path path = Paths.get(DIRECTORY);

        if (!isValidDirectory(path)) {  // 디렉토리 유효성 검사
            return Optional.empty();
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*"+ EXTENSION)) {
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(path.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    User user = (User)ois.readObject();

                    if (user != null && user.getName() != null & user.getName().equals(name)) {
                        return Optional.of(user);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("[Repo]Error reading user file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("[Repo]Error accessing user directory: " + DIRECTORY, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>(); // 모든 User 객체를 담을 리스트
        Path path = Paths.get(DIRECTORY);  // 사용자 디렉토리 경로

        if (!isValidDirectory(path)) {
            System.err.println("[Repo]Warning: User directory is not valid. Returning empty list");
            return allUsers;  //  디렉토리가 유효하지 않으면 빈 리스트를 반환
        }

        // 디렉토리 스트림을 열어 .ser 파일을 순회하도록 구성
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*"+ EXTENSION)) {
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(path.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    User user = (User) ois.readObject();
                    if (user!= null) {
                        allUsers.add(user); // 성공적으로 읽은 User 객체를 리스트에 추가
                    }
                } catch (ClassNotFoundException | IOException e) {
                    // 특정 파일이 손상되었거나 클래스 정의가 없는 경우
                    // 해당 파일만 건너뛰고 다음 파일로 계속 진행(로그 기록은 필수)
                    System.err.println("[Repo]Error reading user file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("[Repo]Error accessing user directory: " + DIRECTORY, e);
        }
        return allUsers;
    }

    @Override
    public void deleteById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try {
            // 파일이 존재하면 삭제하고 true를 반환 / 파일이 존재하지 않으면 아무것도 삭제하지 않고 false를 반환
            if (Files.deleteIfExists(path)) {
                System.out.println("[Repo]User file deleted: " + id);
            } else {
                System.out.println("[Repo]User file not found: " + id);
            }
        } catch (IOException e) {
            System.err.println("[Repo]Error deleting user file: " + id + ". Details: " + e.getMessage());
            throw new RuntimeException("[Repo]Failed to delete user file.", e);
        }
    }


}
