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
        if (!request.users().isEmpty()) {
            for (User user : request.users()) {
                readStatusRepository.save(new ReadStatus(user.getId(), channel.getId()));
            }
        }

        return channelRepository.save(channel);
    }

    public ChannelFindResponse findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("findById : 채널을 찾을 수 없습니다."));

        LinkedList<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .sorted(Comparator.comparingLong(m -> m.getCreatedAt().getEpochSecond()))
                .collect(Collectors.toCollection(LinkedList::new));
        Message message = null;
        if (!messages.isEmpty()) {
            message = messages.getLast();
        }

        ChannelFindResponse response;

        if (channel.getType() == ChannelType.PRIVATE) {
            List<UUID> userIds = new ArrayList<>();
            readStatusRepository.findByChannelId(channelId).forEach(readStatus -> userIds.add(readStatus.getUserId()));

            if (message == null) {
                response = ChannelFindResponse.builder()
                        .name(channel.getName())
                        .description(channel.getDescription())
                        .type(channel.getType())
                        .createdAt(channel.getCreatedAt())
                        .updatedAt(channel.getUpdatedAt())
                        .userIds(userIds)
                        .build();
            } else {
                response = ChannelFindResponse.builder()
                        .name(channel.getName())
                        .description(channel.getDescription())
                        .lastestMessageTime(message.getCreatedAt())
                        .type(channel.getType())
                        .createdAt(channel.getCreatedAt())
                        .updatedAt(channel.getUpdatedAt())
                        .userIds(userIds)
                        .build();
            }

        } else {
            if (message == null) {
                response = ChannelFindResponse.builder()
                        .name(channel.getName())
                        .description(channel.getDescription())
                        .type(channel.getType())
                        .createdAt(channel.getCreatedAt())
                        .updatedAt(channel.getUpdatedAt())
                        .build();
            } else {
                response = ChannelFindResponse.builder()
                        .name(channel.getName())
                        .description(channel.getDescription())
                        .lastestMessageTime(message.getCreatedAt())
                        .type(channel.getType())
                        .createdAt(channel.getCreatedAt())
                        .updatedAt(channel.getUpdatedAt())
                        .build();
            }
        }
        return response;
    }

    public List<ChannelFindResponse> findAllByUserId(UUID userId) {
        List<ChannelFindResponse> channelFindResponses = new ArrayList<>();
        channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .forEach(channel -> channelFindResponses.add(findById(channel.getId())));

        Set<Channel> privateChannels = new HashSet<>();
        for (ReadStatus readStatus : readStatusRepository.findByUserId(userId)) {
            Channel channel = channelRepository.findById(readStatus.getChannelId()).orElse(null);
            if (channel != null && channel.getType() == ChannelType.PRIVATE) {
                privateChannels.add(channel);
            }
        }

        privateChannels.forEach(channel -> channelFindResponses.add(findById(channel.getId())));

        return channelFindResponses;
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
