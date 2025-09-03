package com.codeit.mission.discodeit.mapper;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.Message;
import com.codeit.mission.discodeit.entity.ReadStatus;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private MessageRepository messageRepository;
    private ReadStatusRepository readStatusRepository;
    private UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        List<UserDto> participants = readStatusRepository.findAllByChannelId(channel.getId())
            .stream()
            .map(ReadStatus::getUser)
            .map(userMapper::toDto)
            .toList();

        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
            .stream()
            .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
            .map(Message::getCreatedAt)
            .findFirst()
            .orElse(Instant.MIN);

        return new ChannelDto(channel.getId(), channel.getType(), channel.getName(),
            channel.getDescription(), participants, lastMessageAt);
    }
}
