package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelMapper(MessageRepository messageRepository, ReadStatusRepository readStatusRepository, UserMapper userMapper) {
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.userMapper = userMapper;
    }

    public ChannelDto toDto(Channel channel) {
        List<?> participants = channel.getType() == ChannelType.PRIVATE
            ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(rs -> userMapper.toDto(rs.getUser(), rs.getUser().getStatus() != null ? rs.getUser().getStatus().isOnline() : null))
                .collect(Collectors.toList())
            : Collections.emptyList();

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            (List<com.sprint.mission.discodeit.dto.data.UserDto>) participants
        );
    }
}
