package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;      // 🔹 추가
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException; // 🔹 추가
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    log.info("[CH][CREATE_PUBLIC] name={}", name);

    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);

    log.info("[CH][CREATE_PUBLIC][DONE] id={} name={}", channel.getId(), name);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("[CH][CREATE_PRIVATE] participants={}", request.participantIds());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("[CH][CREATE_PRIVATE][DONE] id={} participants={}", channel.getId(), request.participantIds());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    log.debug("[CH][FIND] id={}", channelId);
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> {
          log.warn("[CH][FIND] not-found id={}", channelId);
          return new ChannelNotFoundException(channelId);     // 🔹 교체
        });
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("[CH][FIND_ALL_BY_USER] userId={}", userId);

    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    List<ChannelDto> result = channelRepository
        .findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();

    log.info("[CH][FIND_ALL_BY_USER][DONE] userId={} total={}", userId, result.size());
    return result;
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    log.info("[CH][UPDATE] id={} newName={}", channelId, newName);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("[CH][UPDATE] not-found id={}", channelId);
          return new ChannelNotFoundException(channelId);     // 🔹 교체
        });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("[CH][UPDATE] private-update-blocked id={}", channelId);
      throw new PrivateChannelUpdateException(channelId);     // 🔹 교체
    }

    channel.update(newName, newDescription);
    log.info("[CH][UPDATE][DONE] id={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.info("[CH][DELETE] id={}", channelId);

    if (!channelRepository.existsById(channelId)) {
      log.warn("[CH][DELETE] not-found id={}", channelId);
      throw new ChannelNotFoundException(channelId);         // 🔹 교체
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    log.info("[CH][DELETE][DONE] id={}", channelId);
  }
}
