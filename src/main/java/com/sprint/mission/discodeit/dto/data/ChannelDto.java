package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserDto> participantIds, // PRIVATE 채널인 경우 참여하는 User의 Id 리스트입니다.
        Instant lastMessageAt // 해당 채널의 가장 마지막 메시지가 생성된 시간입니다. 이 정보와 ReadStatus를 통해 사용자가 각 채널 별로 아직 읽지 않은 메시지가 있는지 확인할 수 있는 기준이 됩니다.
) {
}
