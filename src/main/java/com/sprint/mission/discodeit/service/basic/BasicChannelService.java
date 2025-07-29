package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Optional<Channel> createChannel(String name) {
        // 1. 채널 이름 중복 검사
        Optional<Channel> existingChannel = channelRepository.findByName(name);
        if (existingChannel.isPresent()) {
            System.out.println("[Ser]Channel already exists: " + name);
            return Optional.empty();
        }

        // 2. 새로운 Channel 객체 생성
        Channel newChannel = new Channel(name);

        // 3. 새로운 Channel을 Repository에 저장
        channelRepository.save(newChannel);
        System.out.println("[Ser]Created channel: " + newChannel);
        return Optional.of(newChannel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        System.out.println("[Ser]Finding channel by id: " + id);
        return channelRepository.findById(id);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        System.out.println("[Ser]Finding channel by name: " + name);
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        System.out.println("[Ser]Finding all channels" + channelRepository.findAll().size());
        return channelRepository.findAll();
    }

    @Override
    public void updatedName(UUID id, String name) {
        try {
            if (channelRepository.findByName(name).isPresent()) {
                System.out.println("[Ser]Channel already exists: " + name);
            } else {
                Channel channel = channelRepository.findByName(name).get();
                channel.updateName(name);
                channelRepository.save(new Channel(name));
                System.out.println("[Ser]Updated channel: " + channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        if (channelRepository.findById(id).isPresent()) {
            System.out.println("[Ser]Channel delete: " + id);
            channelRepository.delete(id);
        } else {
            System.out.println("[Ser]Channel not found: " + id);
        }
    }
}
