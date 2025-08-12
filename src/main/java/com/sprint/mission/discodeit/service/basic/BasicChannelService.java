package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
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
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(@Valid ChannelCreateRequest channelCreateRequest) {
        Channel channel = new Channel(ChannelType.PUBLIC, channelCreateRequest.name(), channelCreateRequest.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(ChannelCreateRequest channelCreateRequest) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        if (!channelCreateRequest.userIds().isEmpty()) {
            for (UUID userId : channelCreateRequest.userIds()) {
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

    @Override
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

    @Override
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

    @Override
    public Channel update(@Valid ChannelUpdateRequest channelUpdateRequest) {
        Channel channel = channelRepository.findById(channelUpdateRequest.id())
                .orElseThrow(() -> new NoSuchElementException("update : 채널을 찾을 수 없습니다."));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new SecurityException("update : Private 채널은 업데이트 할 수 없습니다.");
        }
        channel.update(channelUpdateRequest.name(), channelUpdateRequest.description());
        return channelRepository.save(channel);
    }

    @Override
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
