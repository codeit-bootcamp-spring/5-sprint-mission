package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {
    //    UUID id,
//    String username,
//    String email,
//    BinaryContentDto profile,
//    Boolean online

    @Mapping(target = "online", expression ="java(user.getStatus().isOnline())")
    UserDto toDto(User user);
}
