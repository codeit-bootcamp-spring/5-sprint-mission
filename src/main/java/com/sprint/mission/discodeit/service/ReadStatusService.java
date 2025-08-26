package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    /**
     * 사용자가 채널에서 마지막으로 읽은 시각을 갱신
     * - 새로운 메시지를 읽었거나 채널을 열었을 때 호출
     */
    ReadStatus create(UUID userId, UUID channelId, Instant lastReadAt);

    ReadStatus findById(UUID readStatusId, Instant newLastReadAt);

    /**
     * 사용자가 채널에서 마지막으로 읽은 시각을 조회
     * userId와 channelId로 조회
     */
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findByUserId(UUID userId);

    /**
     * 사용자가 속한 모든 채널에 대해 읽지 않은 메시지가 있는지 여부를 반환
     * - 메시지의 마지막 생성 시각과 비교하여 판별
     * - 모든 채널에 대한 "읽지 않음 상태"를 반환하는 리스트
     *
     * @param userId 사용자 ID
     * @return List<ChannelUnreadDto> (채널 ID + hasUnread)
     */
    List<ReadStatusDto.unread> getUnreadChannels(UUID userId);

    /**
     * 특정 채널에 있는 모든 사용자
     * 채널 참여자
     */
    List<UUID> findAllUsers(UUID channelId);

    /**
     * 사용자가 특정 채널에서 아직 읽지 않은 메시지 개수를 반환
     * - 메시지.createdAt > readStatus.lastReadAt 인 메시지 수
     *
     * @param userId    사용자 ID
     * @param channelId 채널 ID
     * @return 읽지 않은 메시지 수
     */
    int countUnreadMessages(UUID userId, UUID channelId);


}
