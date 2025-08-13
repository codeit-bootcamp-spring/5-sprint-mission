package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelAccessibility;
import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @param id            채널 ID
 * @param name          채널 이름 (없을 경우 null 가능)
 * @param description   채널 설명 (없을 경우 null 가능)
 * @param accessibility 채널 접근성 (PUBLIC 또는 PRIVATE)
 * @param userIdList    채널 접근 허용 사용자
 *                      <ul>
 *                           <li>PUBLIC 채널: null 또는 빈 리스트</li>
 *                           <li>PRIVATE 채널: 참여한 사용자 ID 목록</li>
 *                      </ul>
 * @param messages      채널에 보낸 메시지 목록 (없는 경우 null)
 * @param lastMessageAt 채널에서 마지막으로 메시지가 전송된 시각 (없는 경우 null)
 */
public record ChannelResponse(
        UUID id,
        String name,
        String description,
        ChannelAccessibility accessibility,
        List<UUID> userIdList,
        List<Message> messages,
        Instant lastMessageAt
) {}
