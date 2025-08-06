package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public JCFChannelService(JCFChannelRepository channelRepository) {
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
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> readAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(Channel channel) {
        try {
            channelRepository.update(channel.getId(), channel);
            System.out.println("수정 완료: " + channel);
        } catch (NoSuchElementException e) {
            System.out.println("Channel not found");
        }
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        if (channelRepository.existsById(id)) {
            System.out.println("삭제 성공");
            channelRepository.deleteById(id);
        } else {
            System.out.println("Channel not found");
        }
    }
}
