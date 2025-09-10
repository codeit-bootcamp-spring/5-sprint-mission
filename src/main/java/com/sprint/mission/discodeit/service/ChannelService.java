package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.enums.ChannelType;
import com.sprint.mission.discodeit.entity.sub.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    public ChannelDto create(PublicChannelCreateRequest request) {
        Channel channel = Channel.builder()
                .type(ChannelType.PUBLIC)
                .name(request.name())
                .description(request.description())
                .build();

        Channel createdChannel = channelRepository.save(channel);

        return channelMapper.toDto(createdChannel, userMapper);
    }

    public ChannelDto create(PrivateChannelCreateRequest request) {
        Channel channel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
                    return ReadStatus.builder()
                            .user(user)
                            .channel(createdChannel)
                            .lastReadAt(Instant.EPOCH)
                            .build();
                })
                .forEach(readStatusRepository::save);

        return channelMapper.toDto(createdChannel, userMapper);
    }

    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channel ->  channelMapper.toDto(channel, userMapper))
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(channel ->  channelMapper.toDto(channel, userMapper))
                .toList();
    }

    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.setName(newName);
        channel.setDescription(newDescription);
        return channelMapper.toDto(channelRepository.save(channel), userMapper);
    }

    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }
}