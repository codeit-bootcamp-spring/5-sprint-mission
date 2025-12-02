package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ReadStatusMapper readStatusMapper;

    @CacheEvict(value = "readStatuses", key = "#requesterId")
    public ReadStatusDto create(UUID requesterId, ReadStatusCreateRequest request) {
        User user = getUserOrThrow(requesterId);
        Channel channel = getChannelOrThrow(request.channelId());

        ReadStatus savedReadStatus = readStatusRepository.save(
            new ReadStatus(
                user,
                channel,
                request.lastReadAt(),
                channel.getType() == ChannelType.PRIVATE
            )
        );

        return readStatusMapper.toDto(savedReadStatus);
    }

    @Cacheable(value = "readStatuses", key = "#userId")
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @CachePut(value = "readStatuses", key = "#requesterId")
    public ReadStatusDto update(
        UUID readStatusId,
        UUID requesterId,
        ReadStatusUpdateRequest request
    ) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        if (readStatus.getUser() == null
            || !readStatus.getUser().getId().equals(requesterId)) {
            throw new ReadStatusForbiddenException(readStatusId, requesterId);
        }

        if (request.newLastReadAt() != null) {
            ReadStatus updated = readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());
            readStatusRepository.save(updated);
        }

        return readStatusMapper.toDto(readStatus);
    }

    @CacheEvict(value = "readStatuses", allEntries = true)
    public void deleteByChannelId(UUID channelId) {
        readStatusRepository.deleteByChannelId(channelId);
    }

    @CacheEvict(value = "readStatuses", key = "#userId")
    public void deleteByUserId(UUID userId) {
        readStatusRepository.deleteByUserId(userId);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
