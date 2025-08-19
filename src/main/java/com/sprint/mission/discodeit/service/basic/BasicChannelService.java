package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public Channel createPublic(@Valid PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(ChannelType.PUBLIC, publicChannelCreateRequest.name(), publicChannelCreateRequest.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(@Valid PrivateChannelCreateRequest privateChannelCreateRequest) {
        List<UUID> userIds = privateChannelCreateRequest.userIds();

        for (UUID userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new NoSuchElementException("존재하지 않는 유저입니다 : " + userId);
            }
        }

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        for (UUID userId : userIds) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId()));
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
        Map<UUID, ReadStatus> readStatusMap = readStatusRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(ReadStatus::getChannelId, readStatus -> readStatus));

        return channelRepository.findAll().stream()
                .map(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return findById(channel);
                    }
                    if (readStatusMap.containsKey(channel.getId())) {
                        return findById(channel);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Channel update(@Valid PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = channelRepository.findById(publicChannelUpdateRequest.id())
                .orElseThrow(() -> new NoSuchElementException("update : 채널을 찾을 수 없습니다."));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new SecurityException("update : Private 채널은 업데이트 할 수 없습니다.");
        }
        channel.update(publicChannelUpdateRequest.name(), publicChannelUpdateRequest.description());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if(!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("delete : 채널을 찾을 수 없습니다.");
        }

        readStatusRepository.findByChannelId(channelId)
                .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageRepository.deleteById(message.getId()));
        channelRepository.deleteById(channelId);
    }
}
