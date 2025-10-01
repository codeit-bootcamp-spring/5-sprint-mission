package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct 매퍼: ReadStatus 엔티티를 ReadStatusDto로 변환하는 역할 담당
@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface ReadStatusMapper {

    // ReadStatus → ReadStatusDto 변환
    // ReadStatus.user.id → userId 매핑
    // ReadStatus.channel.id → channelId 매핑
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "channelId", source = "channel.id")
    ReadStatusDto toDto(ReadStatus readStatus);
}

