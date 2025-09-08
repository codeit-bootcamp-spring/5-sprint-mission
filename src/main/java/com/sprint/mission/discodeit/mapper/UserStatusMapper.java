package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;


@Component
public class UserStatusMapper {
    public UserStatusDto toDto(UserStatus e) {           // 엔티티→DTO
        if (e == null) return null;                      // 널 가드
        return new UserStatusDto(                        // record 생성
                e.getId(),                                   // id
                e.getUser() != null ? e.getUser().getId() : null, // userId
                e.getLastActiveAt()                          // 마지막 활동
        );                                               // 반환
    }
}
