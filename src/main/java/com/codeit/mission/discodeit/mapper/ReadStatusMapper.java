package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.ReadStatusDto;
import com.codeit.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatusDto toDto(ReadStatus readStatus) {
        return new ReadStatusDto(readStatus.getId(), readStatus.getUser().getId(),
            readStatus.getChannel().getId(), readStatus.getLastReadAt());
    }
}
