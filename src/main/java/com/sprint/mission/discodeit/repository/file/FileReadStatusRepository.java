package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {

        // 저장할 디렉토리가 존재하지 않으면 생성
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("Could not create directory for ReadStatus files.", e);
            }
        }
    }

    private Path resolvePath(UUID userId, UUID channelId) {
        String fileName = userId.toString() + "_" + channelId.toString() + EXTENSION;
        return DIRECTORY.resolve(fileName);
    }

    @Override
    public void save(ReadStatus readStatus) {
        // 파일 경로 가져오기
        Path path = resolvePath(readStatus.getUserId(), readStatus.getChannelId());

        // 객체 직렬화를 통해 ReadStatus 객체를 파일에 사용
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ReadStatus to file.", e);
        }
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        Path path = resolvePath(userId, channelId);

        if (Files.notExists(path)) {
            return null;
        }

        // 파일이 존재하면, 역직렬화를 통해 파일에서 객체를 읽음.
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read ReadStatus from file.", e);
        }
    }
}
