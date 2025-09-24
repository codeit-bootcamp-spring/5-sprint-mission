// src/main/java/com/sprint/mission/discodeit/dto/ReadStatusDto.java

package com.sprint.mission.discodeit.dto.data;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ReadStatusDto {

  private UUID id; // 조회용

  @NotNull(message = "userId는 필수입니다.")
  private UUID userId; // 생성, 조회용

  @NotNull(message = "channelId는 필수입니다.")
  private UUID channelId; // 생성, 조회용

  @NotNull(message = "lastReadAt은 필수입니다.")
  private Instant lastReadAt; // 생성, 조회용
  
  private Instant newLastReadAt; // 수정용
}