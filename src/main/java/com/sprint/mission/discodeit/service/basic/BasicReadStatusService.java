package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    @Override
    public void updateLastReadAt(User user, Channel channel, Instant readAt) {
        ReadStatus status = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                .orElseGet(() -> new ReadStatus(user, channel));

        status.updateLastReadAt(readAt);
        readStatusRepository.save(status);
    }

    @Override
    public void markMessageAsRead(User user, Message message) {
        updateLastReadAt(user, message.getChannel(), message.getCreatedAt());
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId);
    }
}
