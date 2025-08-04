package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.MessageRepository;
import com.sprint.mission.discodeit.respository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public void updateLastReadAt(UUID userId, UUID channelId, Instant readAt) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseGet(() -> new ReadStatus(user, channel));

        readStatus.updateLastReadAt(readAt);
        readStatusRepository.save(readStatus);
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public List<ReadStatusDto.ChannelUnreadStatus> getUnreadChannels(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        return readStatuses.stream()
                .map(rs -> {
                    UUID channelId = rs.getChannelId();
                    Instant lastReadAt = rs.getLastReadAt();

                    Optional<Instant> latestMessageAtOpt = messageRepository.findLastCreatedAtByChannelId(channelId);

                    boolean hasUnread = latestMessageAtOpt
                            .map(latest -> lastReadAt == null || latest.isAfter(lastReadAt))
                            .orElse(false);

                    return new ReadStatusDto.ChannelUnreadStatus(channelId, hasUnread);
                })
                .collect(Collectors.toList());
    }

    @Override
    public int countUnreadMessages(UUID userId, UUID channelId) {
        Instant lastReadAt = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .map(ReadStatus::getLastReadAt)
                .orElse(null);

        if (lastReadAt == null) {
            // 한 번도 읽지 않은 경우 전체 메시지 수 리턴
            return messageRepository.findAllByChannelId(channelId).size();
        } else {
            return messageRepository.findAllByChannelIdAfter(channelId, lastReadAt).size();
        }
    }


    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusRepository.findAllByChannelId(channelId);
    }
}
