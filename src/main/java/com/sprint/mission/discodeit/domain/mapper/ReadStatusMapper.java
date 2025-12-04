package com.sprint.mission.discodeit.domain.mapper;

import com.sprint.mission.discodeit.domain.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatusDto toDto(ReadStatus readStatus) {
        if (readStatus == null) {
            return null;
        }

        return new ReadStatusDto(
            readStatus.getId(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            readStatus.getLastReadAt(),
            readStatus.isNotificationEnabled()
        );
    }
}
