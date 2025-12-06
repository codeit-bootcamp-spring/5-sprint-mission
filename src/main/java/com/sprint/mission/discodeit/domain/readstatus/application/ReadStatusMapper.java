package com.sprint.mission.discodeit.domain.readstatus.application;

import com.sprint.mission.discodeit.domain.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.readstatus.presentation.dto.ReadStatusDto;
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
