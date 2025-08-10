package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponseDto create(Object request) {
        if (request instanceof ChannelCreateRequest) {
            return createPublicChannel((ChannelCreateRequest) request);
        } else if (request instanceof PrivateChannelCreateRequest) {
            return createPrivateChannel((PrivateChannelCreateRequest) request);
        }
        throw new IllegalArgumentException("잘못된 채널 타입입니다.");
    }

    private ChannelResponseDto createPublicChannel(ChannelCreateRequest request) {
        if (channelRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("채널명이 이미 존재합니다: " + request.name());
        }

        Channel channel = new Channel(request.type(), request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        return ChannelResponseDto.fromEntity(savedChannel, null);
    }

    private ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(request.type(), null, null);
        Channel savedChannel = channelRepository.save(channel);

        List<ReadStatus> userReadStatuses = request.userIds().stream()
                .map(userId -> new ReadStatus(UUID.randomUUID(), userId, savedChannel.getId(), Instant.now(), Instant.now()))
                .peek(readStatusRepository::save)
                .toList();

        return ChannelResponseDto.fromEntity(savedChannel, null, request.userIds());
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastMessageTime = messageRepository.findMostRecentMessageTime(channelId)
                .orElseThrow(() -> new NoSuchElementException("No messages found in this channel"));

        if (channel.getType() == ChannelType.PRIVATE) {
            List<UUID> userIds = readStatusRepository.findUserIdsByChannelId(channelId);
            return ChannelResponseDto.fromEntity(channel, lastMessageTime, userIds);
        }

        return ChannelResponseDto.fromEntity(channel, lastMessageTime);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels;

        if (userId == null) {
            channels = channelRepository.findAllPublicChannels();
        } else {
            List<Channel> privateChannels = channelRepository.findByUserId(userId);
            List<Channel> publicChannels = channelRepository.findAllPublicChannels();

            channels = new ArrayList<>();
            channels.addAll(privateChannels);
            channels.addAll(publicChannels);
        }

        return channels.stream()
                .map(channel -> {
                    Instant lastMessageTime = messageRepository.findMostRecentMessageTime(channel.getId())
                            .orElse(null);

                    if (channel.getType() == ChannelType.PRIVATE) {
                        List<UUID> userIds = readStatusRepository.findUserIdsByChannelId(channel.getId());
                        return ChannelResponseDto.fromEntity(channel, lastMessageTime, userIds);
                    }
                    return ChannelResponseDto.fromEntity(channel, lastMessageTime);
                })
                .toList();
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(request.type(), request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);
        return ChannelResponseDto.fromEntity(savedChannel, null);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);

        channelRepository.deleteById(channelId);
    }
}
