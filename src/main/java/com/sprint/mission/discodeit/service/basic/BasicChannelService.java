package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
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

  public ChannelDto.DetailResponse create(ChannelDto.CreateRequest request) {

    Channel channel = null;

    if (request.getType().equals(ChannelType.PRIVATE)) {
      channel = createPrivate(request);
    } else {
      channel = createPublic(request);
    }

    channelRepository.save(channel);

//        if(request.getAdminUserId() != null){
//            readStatusRepository.save(new ReadStatus(request.getAdminUserId(), channel.getId()));
//        }
//
//        if (request.getParticipantIds() != null) {
//            request.getParticipantIds().forEach(userId -> {
//                readStatusRepository.save(new ReadStatus(userId, channel.getId()));
//            });
//        }

    return ChannelDto.DetailResponse.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId()))
        .participantIds(channel.getUserIds())
        .build();
  }

  private Channel createPrivate(ChannelDto.CreateRequest request) {
    // TODO FE에 맞추기위해 일단 다른요소 null 처리
    return new Channel(ChannelType.PRIVATE
        , ""
        , ""
        , null
        , request.getParticipantIds() == null ? List.of() : request.getParticipantIds());
  }

  private Channel createPublic(ChannelDto.CreateRequest request) {

    return new Channel(ChannelType.PUBLIC
        , request.getName()
        , request.getDescription()
        , null
        , request.getParticipantIds() == null ? List.of() : request.getParticipantIds());
  }

  private Instant getLastMessageCreateAt(UUID channelId) {
    List<Message> messages = messageRepository.findAllByChannelId(channelId);

    if (messages.isEmpty()) {
      return null;
    }

    return messages.stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }

  public ChannelDto.DetailResponse findById(UUID id) {

    Channel channel = channelRepository.findById(id).orElse(null);

    if (channel == null) {
      return null;
    }

    return ChannelDto.DetailResponse.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId()))
        .participantIds(channel.getUserIds())
        .build();
  }

  public List<ChannelDto.DetailResponse> findAll() {
    List<Channel> channels = channelRepository.findAll();

    return channels.stream().map(c ->
            ChannelDto.DetailResponse.builder()
                .id(c.getId())
                .type(c.getType())
                .name(c.getName())
                .description(c.getDescription())
                .lastMessageAt(getLastMessageCreateAt(c.getId()))
                .participantIds(c.getUserIds())
                .build())
        .collect(Collectors.toList());
  }

  public List<ChannelDto.DetailResponse> findAllByUserId(UUID userId) {

    List<Channel> channels = channelRepository.findAllByUserId(userId);

    return channels.stream().map(c ->
            ChannelDto.DetailResponse.builder()
                .id(c.getId())
                .type(c.getType())
                .name(c.getName())
                .description(c.getDescription())
                .lastMessageAt(getLastMessageCreateAt(c.getId()))
                .participantIds(c.getUserIds())
                .build())
        .collect(Collectors.toList());
  }

  public ChannelDto.DetailResponse update(ChannelDto.UpdateRequest request) {

    Channel channel = channelRepository.findById(request.getId()).orElse(null);

    if (channel == null || channel.getType().equals(ChannelType.PRIVATE)) {
      return null;
    }

    if (request.getName() != null && request.getDescription() != null) {
      channel.update(request.getName(), request.getDescription());
    }

    if (request.getParticipantIds() != null) {
      request.getParticipantIds().forEach(userId -> {
        if (channel.getUserIds().contains(userId)) {
          return;
        }

        channel.addUser(userId);
      });
    }

    channelRepository.save(channel);

    return ChannelDto.DetailResponse.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .lastMessageAt(getLastMessageCreateAt(channel.getId()))
        .participantIds(channel.getUserIds())
        .build();
  }

  @Override
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id).orElse(null);

    if (channel != null) {
      channelRepository.delete(id);

      messageRepository.findAllByChannelId(id).forEach(m -> {
        messageRepository.delete(m.getId());
      });

      channel.getUserIds().forEach(userId -> {
        readStatusRepository.findAllByUserId(userId)
            .forEach(rs -> {
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
