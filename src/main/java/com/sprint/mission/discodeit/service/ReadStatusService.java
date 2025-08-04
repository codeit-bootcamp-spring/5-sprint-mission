package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.*;

public interface ReadStatusService {
    /**
     * 사용자가 채널에서 마지막으로 읽은 시각을 갱신
     * - 새로운 메시지를 읽었거나 채널을 열었을 때 호출
     */
    void updateLastReadAt(UUID userId, UUID channelId, Instant readAt);


    /**
     * 사용자가 채널에서 마지막으로 읽은 시각을 조회
     * 그러기 위해서는 userId와 channelId로 조회해야함
     */
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);


    /**
     * 사용자가 속한 모든 채널에 대해 읽지 않은 메시지가 있는지 여부를 반환합니다.
     * - 메시지의 마지막 생성 시각과 비교하여 판별
     * - 모든 채널에 대한 "읽지 않음 상태"를 반환하는 리스트
     *
     * @param userId 사용자 ID
     * @return List<ChannelUnreadDto> (채널 ID + hasUnread)
     */
    List<ReadStatusDto.ChannelUnreadStatus> getUnreadChannels(UUID userId);


    /**
     * 사용자가 특정 채널에서 아직 읽지 않은 메시지 개수를 반환합니다.
     * - 메시지.createdAt > readStatus.lastReadAt 인 메시지 수
     *
     * @param userId    사용자 ID
     * @param channelId 채널 ID
     * @return 읽지 않은 메시지 수
     */
    int countUnreadMessages(UUID userId, UUID channelId);


    /**
     * 채널 내 모든 사용자들의 읽음 상태를 반환합니다.
     *
     * @param channelId 채널 ID
     * @return List<ReadStatus>
     */
    List<ReadStatus> findAllByChannelId(UUID channelId);
}
