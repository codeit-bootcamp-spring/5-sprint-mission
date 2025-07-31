package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.*;

public interface ReadStatusService {
    // 메시지 읽음 시각 갱신
    void updateLastReadAt(User user, Channel channel, Instant readAt);

    // 특정 메시지를 읽은 시간으로 간주하고 상태 갱신
    void markMessageAsRead(User user, Message message);

    // 특정 채널에서 사용자의 마지막 읽음 상태 조회
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 특정 채널에 대한 모든 사용자 읽음 상태
    List<ReadStatus> findAllByChannelId(UUID channelId);
}
