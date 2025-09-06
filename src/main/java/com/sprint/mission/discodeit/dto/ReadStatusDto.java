// src/main/java/com/sprint/mission/discodeit/dto/ReadStatusDto.java

package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ReadStatusDto {

  private UUID id; // 조회용
  private UUID userId; // 생성, 조회용
  private UUID channelId; // 생성, 조회용
  private Instant lastReadAt; // 생성, 조회용
  private Instant newLastReadAt; // 수정용
}