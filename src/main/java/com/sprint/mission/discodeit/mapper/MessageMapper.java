package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct 매퍼: Message 엔티티를 MessageDto로 변환하는 역할 담당
@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
// 스프링 빈으로 등록 + BinaryContentMapper, UserMapper를 함께 사용
public interface MessageMapper {

    // Message → MessageDto 변환 시 channelId 필드는 message.channel.id 값으로 매핑
    @Mapping(target = "channelId", source = "channel.id")
    MessageDto toDto(Message message);
}

