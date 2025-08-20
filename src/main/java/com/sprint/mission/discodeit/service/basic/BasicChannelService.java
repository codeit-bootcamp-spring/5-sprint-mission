package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  public ChannelDto.Detail create(CreateCommand create) {

    Channel channel = null;

    if (create.getType().equals(ChannelType.PRIVATE)) {
      channel = createPrivate(create);
    } else {
      channel = createPublic(create);
    }

    channelRepository.save(channel);

    return ChannelDto.Detail.builder().id(channel.getId()).type(channel.getType())
        .name(channel.getName()).description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId())).participantIds(channel.getUserIds())
        .build();
  }

  private Channel createPrivate(CreateCommand create) {

    return new Channel(ChannelType.PRIVATE, "", "", null,
        create.getParticipantIds() == null ? List.of() : create.getParticipantIds());
  }

  private Channel createPublic(CreateCommand create) {

    return new Channel(ChannelType.PUBLIC, create.getName(), create.getDescription(), null,
        create.getParticipantIds() == null ? List.of() : create.getParticipantIds());
  }

  private Instant getLastMessageCreateAt(UUID channelId) {

    List<Message> messages = messageRepository.findAllByChannelId(channelId);

    if (messages.isEmpty()) {
      return null;
    }

    return messages.stream().map(Message::getCreatedAt).max(Instant::compareTo).orElse(null);
  }

  public ChannelDto.Detail findById(UUID id) {

    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not Found"));

    return ChannelDto.Detail.builder().id(channel.getId()).type(channel.getType())
        .name(channel.getName()).description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId())).participantIds(channel.getUserIds())
        .build();
  }

  public List<ChannelDto.Detail> findAll() {

    List<Channel> channels = channelRepository.findAll();

    return channels.stream().map(
        c -> ChannelDto.Detail.builder().id(c.getId()).type(c.getType()).name(c.getName())
            .description(c.getDescription()).lastMessageAt(getLastMessageCreateAt(c.getId()))
            .participantIds(c.getUserIds()).build()).collect(Collectors.toList());
  }

  public List<ChannelDto.Detail> findAllByUserId(UUID userId) {

    List<Channel> channels = channelRepository.findAllByUserId(userId);

    return channels.stream().map(
        c -> ChannelDto.Detail.builder().id(c.getId()).type(c.getType()).name(c.getName())
            .description(c.getDescription()).lastMessageAt(getLastMessageCreateAt(c.getId()))
            .participantIds(c.getUserIds()).build()).collect(Collectors.toList());
  }

  @Override
  public ChannelDto.Detail update(UpdateCommand update) {

    Channel channel = channelRepository.findById(update.getId()).orElse(null);

    if (channel == null || channel.getType().equals(ChannelType.PRIVATE)) {
      throw new RuntimeException("Not Found");
    }

    if (update.getName() != null && update.getDescription() != null) {
      channel.update(update.getName(), update.getDescription());
    }

    if (update.getParticipantIds() != null) {
      update.getParticipantIds().forEach(userId -> {
        if (channel.getUserIds().contains(userId)) {
          return;
        }

        channel.addUser(userId);
      });
    }

    channelRepository.save(channel);

    return ChannelDto.Detail.builder().id(channel.getId()).type(channel.getType())
        .name(channel.getName()).description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId())).participantIds(channel.getUserIds())
        .build();
  }

  @Override
  public void delete(UUID id) {

    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not Found"));

    if (channel != null) {
      channelRepository.delete(id);

      messageRepository.findAllByChannelId(id).forEach(m -> {
        messageRepository.delete(m.getId());
      });

      channel.getUserIds().forEach(userId -> {
        readStatusRepository.findAllByUserId(userId).forEach(rs -> {
          readStatusRepository.delete(rs.getId());
        });
      });
    }
  }

  @Override
  public void deleteAll() {
    channelRepository.deleteAll();
  }
}
