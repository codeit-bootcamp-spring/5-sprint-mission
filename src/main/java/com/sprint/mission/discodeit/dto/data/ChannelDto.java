package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;
import lombok.Data;

@Data
public class ChannelDto {

  private UUID id;
  private String name;
  private String description;
  private ChannelType channelType;

  //수정용 필드
  private String newName;
  private String newDescription;

}
