package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class ChannelDto {

  private UUID id;

  @NotBlank(message = "이름 입력은 필수입니다.")
  private String name;

  @NotBlank(message = "설명 입력은 필수입니다.")
  private String description;

  @NotNull(message = "채널 타입은 필수입니다.")
  private ChannelType channelType;

  //수정용 필드
  private String newName;
  private String newDescription;

}
