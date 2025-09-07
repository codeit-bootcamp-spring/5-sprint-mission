package com.sprint.mission.discodeit.dto.data; // DTO 패키지

import java.time.Instant; // 시간 타입
import java.util.List;    // 리스트
import java.util.UUID;    // UUID
// import에는 주석을 달지 않습니다

public record MessageDto(                // 메시지 응답 DTO
     UUID id,                             // 메시지 ID
     Instant createdAt,                   // 생성 시각(BaseEntity)
     Instant updatedAt,                   // 수정 시각(BaseUpdatableEntity)
     String content,                      // 본문
     UUID channelId,                      // 소속 채널 ID
     UserDto author,                      // 작성자 정보
     List<BinaryContentDto> attachments   // 첨부파일 목록
) {

}                                    // record 본문 없음
