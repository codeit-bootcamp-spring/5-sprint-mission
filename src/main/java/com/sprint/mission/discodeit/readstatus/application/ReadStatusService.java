package com.sprint.mission.discodeit.readstatus.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheName.READ_STATUSES, key = "#requesterId"),
        @CacheEvict(value = CacheName.SUBSCRIBED_CHANNELS, key = "#requesterId")
    })
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

    @Cacheable(value = CacheName.READ_STATUSES, key = "#userId")
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @CacheEvict(value = CacheName.READ_STATUSES, key = "#requesterId")
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

        readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());

        return readStatusMapper.toDto(readStatus);
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
