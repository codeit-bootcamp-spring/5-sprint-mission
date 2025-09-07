package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.main.BaseEntity;
import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.sub.ReadStatus;
import com.sprint.mission.discodeit.enums.ChannelType;
import org.mapstruct.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, builder = @Builder(disableBuilder = false))
public interface ChannelMapper {

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    ChannelDto toDto(Channel channel, @Context UserMapper userMapper);

    @AfterMapping
    default void enrich(Channel channel,
                        @MappingTarget ChannelDto.ChannelDtoBuilder dto,
                        @Context UserMapper userMapper) {
        // 참여자 매핑
        List<UserDto> participants = channel.getReadStatuses().stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto) // UserMapper 재사용
                .toList();

        // 마지막 메시지 시간
        Instant lastMessageAt = channel.getMessages().stream()
                .map(BaseEntity::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // 이름이 없는 PRIVATE 채널은 상대방 username으로 대체
        String channelName = channel.getName();
        if (channelName == null && channel.getType() == ChannelType.PRIVATE) {
            channelName = participants.stream()
                    .findFirst()
                    .map(UserDto::username)
                    .orElse("개인채팅");
        }

        // 빌더에 값 세팅
        dto.participants(participants);
        dto.lastMessageAt(lastMessageAt);
        dto.name(channelName);
    }
}
