package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(resolveParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(resolveLastMessageAt(channel))")
    abstract public ChannelDto toDto(Channel channel);

    protected Instant resolveLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(
                        channel.getId())
                .orElse(Instant.MIN);
    }

    protected List<UserDto> resolveParticipants(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelIdWithUser(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .forEach(participants::add);
        }
        return participants;
    }
}
