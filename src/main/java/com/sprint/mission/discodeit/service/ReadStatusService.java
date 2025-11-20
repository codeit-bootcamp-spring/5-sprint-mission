package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userRepository.getOrThrow(request.userId());
        Channel channel = channelRepository.getOrThrow(request.channelId());

        ReadStatus savedReadStatus = new ReadStatus(user, channel, request.lastReadAt());

        return readStatusMapper.toDto(savedReadStatus);
    }

    @Transactional
    public ReadStatusDto update(
        UUID readStatusId,
        ReadStatusUpdateRequest request
    ) {
        ReadStatus readStatus = readStatusRepository.getOrThrow(readStatusId);

        if (request.newLastReadAt() != null) {
            readStatus.update(request.newLastReadAt());
        }

        return readStatusMapper.toDto(readStatus);
    }
}
