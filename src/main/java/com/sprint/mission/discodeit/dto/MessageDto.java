package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class MessageDto {

  private UUID id;
  private String content;

  private UUID channelId;
  private UUID authorId;

  // 업데이트용 필드
  private String newContent;

}