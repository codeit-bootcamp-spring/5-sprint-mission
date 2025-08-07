package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(String s) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", BinaryContent.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        BinaryContent userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                userNullable = (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(userNullable);
    }

    @Override
    public List<BinaryContent> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public boolean deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean deleteAllByMessageId(UUID messageId) {
        try (Stream<Path> files = Files.list(DIRECTORY)) {
            List<Path> targetFiles = files
                    .filter(path -> path.getFileName().toString().startsWith(messageId.toString()))
                    .toList();

            for (Path path : targetFiles) {
                Files.deleteIfExists(path);
            }

            return !targetFiles.isEmpty(); // true = 삭제된 파일 있음
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete files", e);
        }
    }

    // ✅ 추가: 특정 userId를 가진 BinaryContent 모두 삭제
    @Override
    public void deleteByUserId(UUID userId) {
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .forEach(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            BinaryContent content = (BinaryContent) ois.readObject();
                            if (content.getOwnerId().equals(userId)) {
                                Files.delete(path);
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
