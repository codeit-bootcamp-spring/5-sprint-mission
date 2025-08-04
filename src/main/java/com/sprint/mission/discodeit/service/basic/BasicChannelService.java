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
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        if (dto.type() == null) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        Channel channel = new Channel(dto.name(), dto.type());
        return channelRepository.save(channel);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        channel.updateName(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateTopic(UUID id, String topic) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        channel.updateTopic(topic);
        return channelRepository.save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 채널을 찾을 수 없습니다."));

        channel.updateDescription(description);
        return channelRepository.save(channel);
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}
