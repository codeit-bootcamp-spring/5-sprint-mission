package com.sprint.mission.discodeit.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class MessageDto {

  private UUID id;

  @NotBlank(message = "메세지 내용은 필수입니다.")
  private String content;

  @NotNull(message = "채널 ID는 필수입니다.")
  private UUID channelId;

  @NotNull(message = "작성자 ID는 필수입니다.")
  private UUID authorId;

  // 업데이트용 필드
  private String newContent;

}