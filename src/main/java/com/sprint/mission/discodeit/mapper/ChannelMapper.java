package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserMapper userMapper;


    @Mapping(target = "participants", expression = "java(getParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(getLastMessageAt(channel))")
    abstract public ChannelDto toDto(Channel channel);

    public Instant getLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(channel.getId())
                .orElse(Instant.MIN);
    }

    public List<UserDto> getParticipants(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                    .forEach(participants::add);
        }
        return participants;
    }
}
