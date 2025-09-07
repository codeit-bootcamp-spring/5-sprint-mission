package com.sprint.mission.discodeit.dto.data; // DTO 패키지

import java.time.Instant; // 시간 타입
import java.util.UUID;    // UUID
// import에는 주석을 달지 않습니다

public record ReadStatusDto( // 읽음 상태 DTO
      UUID id,                 // 식별자
      UUID userId,             // 사용자 ID
      UUID channelId,          // 채널 ID
      Instant lastReadAt       // 마지막 읽음 시각
) { }                        // record 본문 없음
