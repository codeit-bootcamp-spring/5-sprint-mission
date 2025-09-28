package com.sprint.mission.discodeit.dto.data; // DTO 패키지

import java.util.UUID; // UUID 타입
// import에는 주석을 달지 않습니다

public record BinaryContentDto( // 바이너리 콘텐츠 응답 DTO를 record로 선언
     UUID id,                    // 파일 식별자
     String fileName,            // 원본 파일명
     Long size,                  // 크기(바이트)
     String contentType         // MIME 타입
) { }                           // record 본문 없음
