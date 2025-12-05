package com.sprint.mission.discodeit.domain.readstatus.mapper;

import com.sprint.mission.discodeit.domain.readstatus.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.domain.readstatus.entity.ReadStatus;
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
