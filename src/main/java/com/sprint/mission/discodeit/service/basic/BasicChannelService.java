package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = searchById(id);
        channel.updateName(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = searchById(id);
        channel.updateDescription(description);
        return  channelRepository.save(channel);
    }

    @Override
    public Channel updateChannelType(UUID id, Channel.ChannelType channelType) {
        Channel channel = searchById(id);
        channel.updateChannelType(channelType);
        return channelRepository.save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        return channelRepository.delete(id).orElseThrow(() -> new NoSuchElementException("해당하는 채널을 찾을 수 없습니다."));
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
    }

    @Override
    public List<Channel> searchByName(String name) {
        if (channelRepository.searchByName(name).isEmpty()) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return channelRepository.searchByName(name);
    }

    @Override
    public Channel searchById(UUID id) {
        return channelRepository.searchById(id).orElseThrow(() -> new NoSuchElementException("해당하는 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> searchAll() {
        return channelRepository.searchAll();
    }
}
