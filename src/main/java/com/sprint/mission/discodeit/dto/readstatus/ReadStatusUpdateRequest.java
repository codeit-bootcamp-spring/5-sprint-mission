package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import lombok.Data;

@Data
public class ReadStatusUpdateRequest {

  private Instant newLastReadAt; // 새로운 읽은 시간으로 수정
}
