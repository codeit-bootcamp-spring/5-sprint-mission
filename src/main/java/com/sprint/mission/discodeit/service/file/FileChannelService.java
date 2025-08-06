package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public FileChannelService(FileChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String name, String description, ChannelType channelType) {
        Channel channel = new Channel(name, description, channelType);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Optional<Channel> readChannel(UUID id) {
        if (channelRepository.existsById(id)) {
            System.out.println("조회 성공: " + channelRepository.findById(id).get());
            return channelRepository.findById(id);
        }
        System.out.println("등록된 회원이 없습니다.");
        return Optional.empty();
    }

    @Override
    public List<Channel> readAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(Channel channel) {
        if (channelRepository.existsById(channel.getId())) {
            System.out.println("수정 완료: " + channel);
            return channelRepository.update(channel.getId(), channel);
        } else {
            System.out.println("수정 실패");
            return null;
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        channelRepository.deleteById(id);
    }
}
