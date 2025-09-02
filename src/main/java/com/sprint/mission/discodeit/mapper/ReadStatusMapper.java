package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto.Detail toDetail(ReadStatus readStatus) {
    if (readStatus == null) {
      return null;
    }

    return ReadStatusDto.Detail.builder()
                               .id(readStatus.getId())
                               .userId(readStatus.getUser()
                                                 .getId())
                               .channelId(readStatus.getChannel()
                                                    .getId())
                               .lastReadAt(readStatus.getLastReadAt())
                               .build();
  }
}
