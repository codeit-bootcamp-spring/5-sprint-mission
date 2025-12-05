package com.sprint.mission.discodeit.domain.service;

import com.sprint.mission.discodeit.common.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.common.exception.readstatus.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.common.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.domain.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.domain.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.domain.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ChannelType;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.domain.repository.ChannelRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import com.sprint.mission.discodeit.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadStatusService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @CacheEvict(value = CacheType.READ_STATUSES, key = "#requesterId")
    public ReadStatusDto create(UUID requesterId, ReadStatusCreateRequest request) {
        log.debug("읽음 상태 생성 요청: userId={}, channelId={}", requesterId, request.channelId());

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

        log.info("읽음 상태 생성 완료: readStatusId={}, userId={}, channelId={}",
            savedReadStatus.getId(), requesterId, request.channelId());

        return readStatusMapper.toDto(savedReadStatus);
    }

    @Cacheable(value = CacheType.READ_STATUSES, key = "#userId")
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @CacheEvict(value = CacheType.READ_STATUSES, key = "#requesterId")
    public ReadStatusDto update(
        UUID readStatusId,
        UUID requesterId,
        ReadStatusUpdateRequest request
    ) {
        log.debug("읽음 상태 수정 요청: readStatusId={}, requesterId={}", readStatusId, requesterId);

        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        if (readStatus.getUser() == null
            || !readStatus.getUser().getId().equals(requesterId)) {
            log.warn("읽음 상태 수정 권한 없음: readStatusId={}, requesterId={}", readStatusId, requesterId);
            throw new ReadStatusForbiddenException(readStatusId, requesterId);
        }

        if (request.newLastReadAt() != null) {
            ReadStatus updated = readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());
            readStatusRepository.save(updated);
            log.info("읽음 상태 수정 완료: readStatusId={}, lastReadAt={}", readStatusId, request.newLastReadAt());
        }

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
