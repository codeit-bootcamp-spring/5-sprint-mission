package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public JCFChannelService() {
        this.channelRepository = new JCFChannelRepository();
    }

    @Override
    public Channel save(Channel channelDto) {
        validate(channelDto);
        return channelRepository.save(channelDto);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, Channel channelDto) {
        validate(channelDto);
        Channel ch = findById(id);
        return channelRepository.update(ch.getId(), channelDto);
    }

    @Override
    public void delete(UUID id) {
        Channel ch = findById(id);
        channelRepository.delete(ch.getId());
    }

    private void validate(Channel chDto) {
        if (chDto == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }
        if (chDto.getName() == null || chDto.getName().isBlank()) {
            throw new IllegalArgumentException("Channel name is required");
        }
        if (chDto.getDescription() == null) {
            throw new IllegalArgumentException("Channel description must not be null");
        }
    }
}
