package com.sprint.mission.discodeit.readstatus.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusMapper readStatusMapper;

    private final CacheService cacheService;

    @Transactional
    @CacheEvict(value = CacheName.READ_STATUSES, key = "#requesterId")
    public ReadStatusDto create(UUID requesterId, ReadStatusCreateRequest request) {
        UUID channelId = request.channelId();

        log.debug("Creating ReadStatus: [requesterId={}, channelId={}]", requesterId, channelId);

        User user = userRepository.findById(requesterId)
            .orElseThrow(() -> new UserNotFoundException(requesterId));
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

        Optional<ReadStatus> existingReadStatus = readStatusRepository.findByUserIdAndChannelId(requesterId, channelId);
        if (existingReadStatus.isPresent()) {
            ReadStatusDto result = readStatusMapper.toDto(existingReadStatus.get());

            log.info("ReadStatus already exists: [readStatusId={}, userId={}, channelId={}]",
                result.id(), result.userId(), result.channelId());

            return result;
        }

        ReadStatus savedReadStatus = readStatusRepository.save(
            new ReadStatus(
                user,
                channel,
                request.lastReadAt(),
                channel.getType() == ChannelType.PRIVATE
            )
        );

        ReadStatusDto result = readStatusMapper.toDto(savedReadStatus);

        if (channel.getType() == ChannelType.PRIVATE) {
            cacheService.evict(CacheName.SUBSCRIBED_CHANNELS, requesterId);
        }

        log.info("ReadStatus created: [readStatusId={}, userId={}, channelId={}]",
            result.id(), result.userId(), result.channelId());

        return result;
    }

    @Cacheable(value = CacheName.READ_STATUSES, key = "#userId")
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.debug("[Cache Miss] find all read statuses: [userId={}]", userId);

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
        log.debug("Updating ReadStatus: [readStatusId={}, requesterId={}]",
            readStatusId, requesterId);

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

        if (!readStatus.getUser().getId().equals(requesterId)) {
            throw new ReadStatusForbiddenException(readStatusId, requesterId);
        }

        readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());

        ReadStatusDto result = readStatusMapper.toDto(readStatus);

        log.info("ReadStatus updated: [readStatusId={}, userId={}]", result.id(), result.userId());

        return result;
    }
}
