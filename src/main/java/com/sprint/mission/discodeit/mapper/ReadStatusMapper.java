package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;


@Component
public class ReadStatusMapper {
    public ReadStatusDto toDto(ReadStatus e) {           // 엔티티→DTO
        if (e == null) return null;                      // 널 가드
        return new ReadStatusDto(                        // record 생성
                e.getId(),                                   // id
                e.getUser() != null ? e.getUser().getId() : null,       // userId
                e.getChannel() != null ? e.getChannel().getId() : null, // channelId
                e.getLastReadAt()                            // 마지막 읽음
        );                                               // 반환
    }
}
