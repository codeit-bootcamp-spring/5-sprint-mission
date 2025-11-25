package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        User user = getUserOrThrow(request.userId());
        Channel channel = getChannelOrThrow(request.channelId());

        ReadStatus savedReadStatus = new ReadStatus(user, channel, request.lastReadAt());

        return readStatusMapper.toDto(savedReadStatus);
    }

    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Transactional
    public ReadStatusDto update(
        UUID readStatusId,
        ReadStatusUpdateRequest request
    ) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        if (request.newLastReadAt() != null) {
            readStatus.update(request.newLastReadAt());
        }

        return readStatusMapper.toDto(readStatus);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId).orElseThrow(ChannelNotFoundException::new);
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId).orElseThrow(ReadStatusNotFoundException::new);
    }
}
