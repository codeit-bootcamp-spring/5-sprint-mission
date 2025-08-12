package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("channelService")
@RequiredArgsConstructor
@Validated
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    public Channel createPublic(@Valid ChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    public Channel createPrivate(ChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        if (!request.userIds().isEmpty()) {
            for (UUID userId : request.userIds()) {
                readStatusRepository.save(new ReadStatus(userId, channel.getId()));
            }
        }

        return channelRepository.save(channel);
    }

    public ChannelFindResponse findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("findById : 채널을 찾을 수 없습니다."));

        return findById(channel);
    }

    public ChannelFindResponse findById(Channel channel) {
        UUID channelId = channel.getId();

        Instant lastestMessageTime = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparingLong(m -> m.getCreatedAt().getEpochSecond()))
                .map(Message::getCreatedAt)
                .orElse(null);

        return ChannelFindResponse.builder()
                .name(channel.getName())
                .userIds(channel.getType() != ChannelType.PRIVATE ?
                        null : readStatusRepository.findByChannelId(channelId).stream()
                        .map(ReadStatus::getUserId)
                        .toList())
                .description(channel.getDescription())
                .type(channel.getType())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastestMessageTime(lastestMessageTime)
                .build();
    }

    public List<ChannelFindResponse> findAllByUserId(UUID userId) {
        List<ChannelFindResponse> channelFindResponses = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .map(this::findById)
                .collect(Collectors.toCollection(ArrayList::new));

        readStatusRepository.findByUserId(userId).stream()
                .filter(readStatus -> findById(readStatus.getChannelId()).type().equals(ChannelType.PRIVATE))
                .forEach(readStatus -> channelFindResponses.add(findById(readStatus.getChannelId())));

        return List.copyOf(channelFindResponses);
    }

    public Channel update(@Valid ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("update : 채널을 찾을 수 없습니다."));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new SecurityException("update : Private 채널은 업데이트 할 수 없습니다.");
        }
        channel.update(request.name(), request.description());
        return channelRepository.save(channel);
    }

    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("delete : 채널을 찾을 수 없습니다."));

        readStatusRepository.findByChannelId(channelId)
                .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageRepository.deleteById(message.getId()));
        channelRepository.deleteById(channelId);
    }

    public void deleteAll() {
        channelRepository.findAll().forEach(channel -> delete(channel.getId()));
    }
}
