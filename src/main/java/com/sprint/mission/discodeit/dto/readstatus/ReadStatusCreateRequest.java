package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ReadStatusCreateRequest {

  private UUID userId; //어떤 user가
  private UUID channelId; //어떤 채널에서
  private Instant lastReadAt; // 언제 마지막으로 읽었는지
}
