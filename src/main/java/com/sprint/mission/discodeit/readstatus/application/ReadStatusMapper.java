package com.sprint.mission.discodeit.readstatus.application;

import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
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
