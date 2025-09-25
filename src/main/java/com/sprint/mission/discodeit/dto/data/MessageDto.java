package com.sprint.mission.discodeit.dto.data;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class MessageDto {

  private UUID id;

  @NotBlank(message = "메세지 내용은 필수입니다.")
  private String content;

  private UUID channelId;

  private UUID authorId;

  // 업데이트용 필드
  private String newContent;

}