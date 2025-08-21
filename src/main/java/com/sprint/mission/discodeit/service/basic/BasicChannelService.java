package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public Channel addPublicChannel(AddPublicChannelRequest addPublicChannelRequest) {
    Channel channel = new Channel(
        addPublicChannelRequest.channelName(),
        addPublicChannelRequest.ownerUserId(),
        ChannelType.PUBLIC,
        addPublicChannelRequest.channelDescription()
    );
    Optional<Channel> addedChannel = channelRepository.save(channel);
    readStatusRepository.save(
        new ReadStatus(addPublicChannelRequest.ownerUserId(), channel.getId()));
    return addedChannel.orElseThrow();
  }

  @Override
  public Channel addPrivateChannel(AddPrivateChannelRequest addPrivateChannelRequest) {
    Channel channel = new Channel(
        null,
        addPrivateChannelRequest.ownerUserId(),
        ChannelType.PRIVATE,
        null
    );

    readStatusRepository.save(
        new ReadStatus(addPrivateChannelRequest.ownerUserId(), channel.getId()));

    Optional<Channel> addedChannel = channelRepository.save(channel);
    return addedChannel.orElseThrow();
  }

  @Override
  public GetChannelByIdResponse getChannelById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId).orElseThrow();

    List<UUID> usersIdByChannelId = null;
    if (channel.getChannelType() == ChannelType.PRIVATE) {
      usersIdByChannelId = readStatusRepository.findUsersIdByChannelId(channel.getId());
    }

    List<Message> messages = messageRepository.findAllByChannelId(channelId);

    Instant recentMessageTime = null;
    for (Message message : messages) {
      Instant createdAt = message.getCreatedAt();
      if (recentMessageTime == null || createdAt.isAfter(recentMessageTime)) {
        recentMessageTime = createdAt;
      }
    }

    return new GetChannelByIdResponse(channel, recentMessageTime, usersIdByChannelId);
  }

  @Override
  public List<GetChannelByIdResponse> getAllChannelByUserId(UUID userId) {
    List<Channel> targetChannel = new ArrayList<>();
    List<UUID> channelsIdByUserId = readStatusRepository.findChannelsIdByUserId(userId);

    for (UUID channelId : channelsIdByUserId) {
      channelRepository.findById(channelId).ifPresent(targetChannel::add);
    }

    List<GetChannelByIdResponse> resultList = new ArrayList<>();

    for (Channel channel : targetChannel) {
      List<UUID> usersIdByChannelId = null;

      if (channel.getChannelType() == ChannelType.PRIVATE) {
        usersIdByChannelId = readStatusRepository.findUsersIdByChannelId(channel.getId());
      }
      List<Message> messages = messageRepository.findAllByChannelId(channel.getId());

      Instant recentMessageTime = null;
      for (Message message : messages) {
        Instant createdAt = message.getCreatedAt();
        if (recentMessageTime == null || createdAt.isAfter(recentMessageTime)) {
          recentMessageTime = createdAt;
        }
      }

      resultList.add(new GetChannelByIdResponse(channel, recentMessageTime, usersIdByChannelId));
    }
    return resultList;
  }

  @Override
  public Channel updateChannel(UpdateChannelRequest updateChannelRequest, UUID channelId) {
    Channel channel = channelRepository.findById(channelId).orElseThrow();
    if (channel.getChannelType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("PRIVATE타입의 채널은 업데이트가 불가하다.");
    }

    channel.updateChannelName(updateChannelRequest.channelName());
    channel.updateDescription(updateChannelRequest.channelDescription());
    channelRepository.save(channel);
    return channel;
  }

  @Override
  public void deleteChannel(UUID channelId) {
    channelRepository.delete(channelId);
    readStatusRepository.deleteByChannelId(channelId);
    messageRepository.deleteByChannelId(channelId);
  }

  @Override
  public void deleteAllChannel() {
    channelRepository.deleteAll();
  }


}
