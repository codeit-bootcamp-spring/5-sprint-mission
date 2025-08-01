package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Channel create(ChannelDto.Create dto) {
        Channel channel = new Channel(dto.name(), dto.type());
        return channelRepository.save(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelRepository.findById(id));
    }

    @Override
    public List<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다.");
        }
        channel.updateName(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateTopic(UUID id, String topic) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 ID의 토픽이 존재하지 않습니다.");
        }
        channel.updateTopic(topic);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("채널이 존재하지 않습니다.");
        }
        channel.updateDescription(description);
        return channelRepository.save(channel);
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}
