package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel create(ChannelType type, String name, UUID authorId, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("[!] 채널 이름이 null 이거나 비어있을 수 없습니다.");
        }
        Channel channel = new Channel(type, name, authorId, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[!] 해당 channel이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> searchByName(String token) {
        return findAll().stream().filter(c -> (c.getName().contains(token))).toList();
    }

    @Override
    public Channel update(UUID id, UUID requestId, String newName, String newDescription) {
        Channel channel = find(id);

        if (!channel.getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 수정 권한이 없습니다.");
        }
        channel.update(newName, newDescription);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id, UUID requestId) {
        if (!channelRepository.existsById(id)) {
            throw new NoSuchElementException("[!] 채널이 존재하지 않습니다.");
        }
        Channel channel = find(id);
        if (!channel.getAuthorId().equals(requestId)) {
            throw new IllegalArgumentException("[!] 삭제 권한이 없습니다.");
        }

        channelRepository.deleteById(id);
    }

}