package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct 매퍼: UserStatus 엔티티를 UserStatusDto로 변환하는 역할 담당
@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface UserStatusMapper {

    // UserStatus → UserStatusDto 변환
    // UserStatus.user.id → userId 필드에 매핑
    @Mapping(target = "userId", source = "user.id")
    UserStatusDto toDto(UserStatus userStatus);
}

