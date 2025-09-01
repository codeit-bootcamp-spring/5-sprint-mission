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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadStatusService {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest req) {
        User u = userRepository.getOrThrow(req.userId());
        Channel c = channelRepository.getOrThrow(req.channelId());
        return readStatusMapper.toDto(
            readStatusRepository.save(new ReadStatus(u, c, req.lastReadAt()))
        );
    }

    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest req) {
        ReadStatus rs = readStatusRepository.getOrThrow(readStatusId);
        if (req.newLastReadAt() != null) {
            rs.setLastReadAt(req.newLastReadAt());
        }
        return readStatusMapper.toDto(rs);
    }
}
