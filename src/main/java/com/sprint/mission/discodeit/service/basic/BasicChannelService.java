package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Channel create(String name, String description, ChannelType type) {
        Channel channel = new Channel(name,
                description,
                type
        );

        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        Optional<Channel> channel = channelRepository.findById(id);
        return channel.orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description, ChannelType type) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
        channel.update(name, description, type);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
        channelRepository.deleteById(channel.getId());
    }
}
