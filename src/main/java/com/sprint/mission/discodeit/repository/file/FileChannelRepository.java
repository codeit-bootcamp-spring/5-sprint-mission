package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final String DIRECTORY;
    private final String EXTENSION;

    // FileChannelRepository의 클래스 생성자
    // 저장 디렉토리 설정("CHANNEL")
    // 파일 확장자 설정 (".ser");
    // User와 동일하게 저장할 디렉토리가 존재하지 않으면 생성 / 예외발생 시 RuntimeException 처리
    public FileChannelRepository() {
        this.DIRECTORY = "CHANNEL";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 디렉토리가 존재하고, 해당 디렉토리가 맞는 검증
    // 디렉토리가 존재하거나, 존재하더라도 디렉토리가 아닌 경우를 확인하는 목적이기 때문에 || 활용
    private boolean isValidDirectory(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            System.err.println("User directory does not exist ro is not a directory: " + DIRECTORY);
            return false;
        }
        return true;
    }


    @Override
    public Channel save(Channel channel) {
        Path path = Paths.get(DIRECTORY, channel.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, id + toString() + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            channel = (Channel) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + channel.getId() + ".Details" + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(channel);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        Path path = Paths.get(DIRECTORY);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*" + EXTENSION)) {
            // "CHANNEL" 디렉토리 내의 모든 .ser 파일 순회
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(entry.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Channel channel = (Channel) ois.readObject();

                    // 불러온 Channel 객체를 Channel이름과 비교
                    if (channel != null && channel.getName() != null && channel.getName().equals(name)) {
                        return Optional.of(channel);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("Error reading channel file " + entry.getFileName() + "Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error accessing channel directory: " + DIRECTORY, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> allChannels = new ArrayList<>(); // 모든 Channel 객체를 담을 리스트
        Path path = Paths.get(DIRECTORY);

        if (!isValidDirectory(path)) {
            System.out.println("Warning: Channel directory does not exist ro is not a directory: " + DIRECTORY);
            return allChannels;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*" + EXTENSION)) {
            for (Path entry : stream) {
                try (FileInputStream fis = new FileInputStream(path.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Channel channel = (Channel) ois.readObject();
                    if (channel != null) {
                        allChannels.add(channel); // 성공적으로 읽은 Chanenl 객체를 리스트에 추가
                    }
                } catch (ClassNotFoundException | IOException e) {
                    // 특정 파일이 손상되었거나 클래스 정의가 없는 경우
                    // 해당 파일만 건너뛰고 다음 파일로 계속 진행(로그 기록은 필수)
                    System.err.println("Error reading Channel file: " + entry.getFileName() + ".Skipping. Details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error accessing Channel directory: " + DIRECTORY, e);
        }
        return allChannels;
    }

    @Override
    public void delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try {
            if (Files.deleteIfExists(path)) {
                System.out.println("Channel file deleted: " + id);
            } else {
                System.out.println("Channel file not found: " + id);
            }
        } catch (IOException e) {
            System.err.println("Error deleting Channel file: " + id + ". Details: " + e.getMessage());
            throw new RuntimeException("Failed to delete Channel file.", e);
        }
    }
}
