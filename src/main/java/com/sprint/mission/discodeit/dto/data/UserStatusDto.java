package com.sprint.mission.discodeit.dto.data; // DTO 패키지

import java.time.Instant; // 시간 타입
import java.util.UUID;    // UUID
// import에는 주석을 달지 않습니다

public record UserStatusDto( // 사용자 상태 DTO
     UUID id,                 // 상태 ID
     UUID userId,             // 사용자 ID
     Instant lastActiveAt     // 마지막 활동 시각
) { }                        // record 본문 없음
