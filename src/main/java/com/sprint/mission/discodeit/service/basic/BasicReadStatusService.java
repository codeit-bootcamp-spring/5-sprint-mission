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
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 채널 읽기 상태가 존재하지 않습니다."));
    }

    @Override
    public List<ReadStatusDto.ChannelUnreadStatus> getUnreadChannels(UUID userId) {
        // 모든 채널에 대한 읽음 상태 조회
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        return readStatuses.stream()
                .map(rs -> {
                    UUID channelId = rs.getChannelId(); // 채널 아이디 추출

                    // 해당 채널에서 마지막으로 생성된 메시지 시간
                    Optional<Instant> latestMessageAtOpt = messageRepository.findLastCreatedAtByChannelId(channelId);

                    // 안읽은 메시지가 있는지
                    boolean hasUnread = latestMessageAtOpt
                            .map(latest -> !rs.isRead(latest)) // 최신 메시지가 읽음 상태인지 확인
                            .orElse(false); // 없으면 읽음 처리

                    // 채널 Id와 읽음 상태를 반환
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
