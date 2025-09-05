package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper; // ✅ 매퍼 주입

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    return channelRepository.save(new Channel(ChannelType.PUBLIC, request.name(), request.description()));
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel createdChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    request.participantIds().stream()
        .map(id -> userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found")))
        .map(u -> new ReadStatus(u, createdChannel, Instant.now()))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto) // ✅ 매퍼 사용
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    Set<UUID> mySubscribed = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(rs -> rs.getChannel().getId())
        .collect(Collectors.toSet());

    return channelRepository.findAll().stream()
        .filter(ch -> ch.getType() == ChannelType.PUBLIC || mySubscribed.contains(ch.getId()))
        .map(channelMapper::toDto) // ✅ 매퍼 사용
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(request.newName(), request.newDescription());
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());
    channelRepository.deleteById(channelId);
  }
}
