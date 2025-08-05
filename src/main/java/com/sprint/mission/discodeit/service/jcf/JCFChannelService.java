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
    public Channel create(Channel channel) {

        if (channel == null) {
            return null;
        }

        return channelRepository.save(channel);
    }

    @Override
    public Channel create(ChannelType type, String name, String description, UUID adminUserId) {

        if (type == null || name == null || adminUserId == null) {
            return null;
        }

        return channelRepository.save(new Channel(type, name, description, adminUserId));
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel get(UUID id) {
        return channelRepository.findById(id).orElse(null);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = channelRepository.findById(id).orElse(null);

        if (channel == null) {
            return null;
        }

        channel.update(name, description);
        return channelRepository.save(channel);
    }

    // TODO 추후 불필요 메서드 제거

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
