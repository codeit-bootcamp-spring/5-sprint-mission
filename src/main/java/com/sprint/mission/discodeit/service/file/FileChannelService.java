package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository = new FileChannelRepository();

    @Override
    public Channel register(Channel channel) {
        if (isInvalid(channel.getName()) || isInvalid(channel.getDescription()))
            throw new IllegalArgumentException("채널 등록에 실패했습니다.");
        System.out.println("채널 : " + channel.getName() + " 등록 성공.");
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id).orElseThrow(() -> new RuntimeException("채널에서 해당 " + id + "를 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String newDescription) {
        if (isInvalid(newDescription))
            throw new IllegalArgumentException("새로운 채널 설명을 입력하세요.");

        Path path = Path.of("CHANNEL").resolve(id + ".ser");
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Channel channel = (Channel) ois.readObject();
            channel.setUpdatedAt(System.currentTimeMillis());
            channel.setDescription(newDescription);
            return channelRepository.save(channel);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel delete(UUID id) {
        return channelRepository.delete(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

}
