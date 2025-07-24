package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    // 저장 방식(JCF 또는 File)에 따라 적절한 구현체를 주입받아 사용
    public BasicChannelService(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String name, String topic) {
        Channel channel = new Channel(name, topic);
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
    public void deleteById(UUID id) {
        channelRepository.deleteById(id);
    }
}
