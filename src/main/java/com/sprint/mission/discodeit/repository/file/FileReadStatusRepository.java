package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path directory;
    private static final String EXTENSION = ".ser";

    public FileReadStatusRepository(String folderName) {
        this.directory = Paths.get(System.getProperty("user.dir"), "file-data-map", folderName);
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    private Stream<Path> serFiles() {
        try {
            return Files.list(directory).filter(p -> p.getFileName().toString().endsWith(EXTENSION));
        } catch (IOException e) {
            throw new RuntimeException("파일 목록 조회 실패", e);
        }
    }

    private ReadStatus loadFromPath(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("ReadStatus 로드 실패: " + path, e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("ReadStatus 저장 실패", e);
        }
        return readStatus;
    }
    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) return Optional.empty();
        return Optional.of(loadFromPath(path));
    }

    @Override
    public List<ReadStatus> findAll() {
        try (Stream<Path> s = serFiles()) {
            return s.map(this::loadFromPath).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        try (Stream<Path> s = serFiles()) {
            return s.map(this::loadFromPath)
                    .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                    .findFirst();
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        try (Stream<Path> s = serFiles()) {
            return s.map(this::loadFromPath)
                    .filter(rs -> rs.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        try (Stream<Path> s = serFiles()) {
            return s.map(this::loadFromPath)
                    .filter(rs -> rs.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        }
    }

    // 존재 여부
    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findByUserIdAndChannelId(userId, channelId).isPresent();
    }

    // D
    @Override
    public boolean deleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("ID로 ReadStatus 삭제 실패", e);
        }
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(rs -> deleteById(rs.getId()));
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        findAllByUserId(userId).forEach(rs -> deleteById(rs.getId()));
    }

    // ★ 인터페이스에 추가된 메서드 구현
    @Override
    public void deleteAll() {
        try (Stream<Path> s = serFiles()) {
            s.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException("전체 삭제 중 실패: " + path, e);
                }
            });
        }
    }
}


