package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final JCFChannelRepository channelRepository;

    public JCFChannelService(JCFChannelRepository repository) {
        this.channelRepository = repository;
    }

    @Override
    public ChannelDto.DetailResponse create(ChannelDto.CreateRequest request) {
        return null;
    }

    @Override
    public ChannelDto.DetailResponse update(ChannelDto.UpdateRequest request) {
        return null;
    }

    @Override
    public ChannelDto.DetailResponse findById(UUID id) {
        return null;
    }

    @Override
    public List<ChannelDto.DetailResponse> findAll() {
        return List.of();
    }

    @Override
    public List<ChannelDto.DetailResponse> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id).orElse(null);

        if (channel != null) {
            channelRepository.delete(id);
        }
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
    }
}
