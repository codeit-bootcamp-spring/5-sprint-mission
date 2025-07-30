package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.Files.list;

public class FileChannelService implements ChannelService {

    private final Path directory;

    public FileChannelService() {
        directory = Paths.get(System.getProperty("user.dir"), "channel_data");
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch (IOException e) {
                throw new RuntimeException("channel_data 폴더 생성 실패");
            }
        }
    }

    @Override
    public Channel create(Channel channel) {
        if (channel == null) {
            System.err.println("오류 : Channel 생성에 실패. channel이 null 입니다.");
            return null;
        }
        Path filePath = directory.resolve(channel.getChannelId() + ".ser");
        if (Files.exists(filePath)) {
            System.out.println("오류 : 이미 존재하는 Channel ID 입니다." + channel.getChannelId());
            return null;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException("Channel 파일 생성 실패", e);
        }
        System.out.println("channel 생성 : " + channel);
        return  channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        if (channelId == null) {
            System.err.println("오류 : findById에 실패. channelId가 null 입니다.");
            return Optional.empty();
        }
        Path filePath = directory.resolve(channelId + ".ser");
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Channel channel = (Channel) ois.readObject();
            return Optional.of(channel);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("오류 : Channel 역직렬화 실패 : (" + channelId + ")" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Channel> findAll() {
        if (!Files.exists(directory)) {
            return List.of();
        }
        try {
            List<Channel> channelList = new ArrayList<>();
            list(directory).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    Channel channel = (Channel) ois.readObject();
                    channelList.add(channel);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("오류: Channel 파일 로딩 실패 (" + path.getFileName() + "): " + e.getMessage());
                }
            });
            return channelList;
        } catch (IOException e) {
            throw new RuntimeException("channel_data 폴더 목록 조회 실패", e);
        }
    }

    @Override
    public Optional<Channel> update(UUID channelId, Channel updatedChannel) {
        if (channelId == null ||  updatedChannel == null) {
            System.err.println("오류: update 실패. channelId 또는 updatedChannel이 null 입니다.");
            return Optional.empty();
        }
        Path filePath = directory.resolve(channelId + ".ser");
        if (!Files.exists(filePath)) {
            return Optional.empty();

        } try(ObjectOutputStream oos =new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(updatedChannel);
        } catch (IOException e) {
            System.err.println("오류 : Channel 업데이트 실패: "  + filePath + " / "+ e.getMessage());
            return Optional.empty();
        }
        System.out.println("updatedChannel : " + updatedChannel);
        return  Optional.of(updatedChannel);
    }

    @Override
    public boolean delete(UUID channelId) {
        if (channelId == null) {
            System.err.println("오류: delete 실패. channelId가 null 입니다.");
            return false;
        }
        Path filePath = directory.resolve(channelId + ".ser");
        try {
            boolean deletedChannelId = Files.deleteIfExists(filePath);
            if (deletedChannelId) {
                System.out.println("channelId : " + channelId);
            } else {
                System.out.println("channelId 삭제 실패 : " + channelId + "파일이 존재 하지 않습니다.");
            }
            return deletedChannelId;

        } catch (IOException e) {
            System.err.println("오류 : Channel 파일 삭제 실패: "  + filePath + " / "+ e.getMessage());
            return false;
        }
    }
}
