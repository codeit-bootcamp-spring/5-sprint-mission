package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct 매퍼: User 엔티티를 UserDto로 변환하는 역할 담당
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
// 스프링 빈 등록 + BinaryContentMapper, UserStatusMapper 함께 사용
public interface UserMapper {

    // User → UserDto 변환
    // User.status.isOnline() 값을 UserDto.online 필드에 매핑
    @Mapping(target = "online", expression = "java(user.getStatus().isOnline())")
    UserDto toDto(User user);
}

