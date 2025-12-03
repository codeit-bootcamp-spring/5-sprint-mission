package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;

  @Transactional
  @Override
  @CacheEvict(value = "channel-list", allEntries = true)
  public ChannelDto.Detail create(CreateCommand create) {

    Channel channel = Channel.builder()
                             .type(create.getType())
                             .description(create.getType()
                                                .equals(ChannelType.PRIVATE) ? ""
                                 : create.getDescription())
                             .name(create.getType()
                                         .equals(ChannelType.PRIVATE) ? "" : create.getName())
                             .build();

    channelRepository.save(channel);

    if (create.getType()
              .equals(ChannelType.PRIVATE)) {

      List<User> users = userRepository.findAllById(create.getParticipantIds());
      List<ReadStatus> readStatuses = users.stream()
                                           .map(user -> ReadStatus.builder()
                                                                  .channel(channel)
                                                                  .user(user)
                                                                  .notificationEnabled(true)
                                                                  .build())
                                           .toList();
      readStatusRepository.saveAll(readStatuses);

      channel.getReadStatuses()
             .addAll(readStatuses);
    }

    log.info("Channel created: {}", channel);

    return channelMapper.toDetail(channel);
  }

  public ChannelDto.Detail findById(UUID id) {

    Channel channel = channelRepository.findById(id)
                                       .orElseThrow(() -> new ChannelNotFoundException(id));

    return channelMapper.toDetail(channel);
  }

  public List<ChannelDto.Detail> findAll() {

    List<Channel> channels = channelRepository.findAll();

    return channels.stream()
                   .map(channelMapper::toDetail)
                   .toList();
  }

  @Cacheable(value = "channel-list", key = "#userId")
  public List<ChannelDto.Detail> findAllByUserId(UUID userId) {

    List<Channel> channels = new java.util.ArrayList<>();

    channels.addAll(channelRepository.findByType(ChannelType.PUBLIC));
    channels.addAll(readStatusRepository.findByUserId(userId)
                                        .stream()
                                        .map(ReadStatus::getChannel)
                                        .toList());

    return channels.stream()
                   .distinct()
                   .map(channelMapper::toDetail)
                   .toList();
  }

  @Transactional
  @Override
  @CachePut(value = "channel-list", key = "#update.id")
  public ChannelDto.Detail update(UpdateCommand update) {

    Channel channel = channelRepository.findById(update.getId())
                                       .orElseThrow(
                                           () -> new ChannelNotFoundException(update.getId()));

    if (channel.getType()
               .equals(ChannelType.PRIVATE)) {
      throw new PrivateChannelUpdateNotAllowedException(update.getId());
    }

    if (update.getName() != null && update.getDescription() != null) {
      channel.update(update.getName(), update.getDescription());
    }

    if (update.getParticipantIds() != null) {
      // TODO 적용 될 케이스가 없음...
      update.getParticipantIds()
            .forEach(userId -> {
              if (channel.getReadStatuses()
                         .stream()
                         .anyMatch(rs -> rs.getUser()
                                           .getId()
                                           .equals(userId))) {
                return;
              }

              User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new UserNotFoundException(userId));

              readStatusRepository.save(ReadStatus.builder()
                                                  .user(user)
                                                  .lastReadAt(null)
                                                  .build());
            });
    }

    log.info("Channel updated: {}", channel);

    return channelMapper.toDetail(channel);
  }

  @Transactional
  @Override
  @CacheEvict(value = "channel-list", allEntries = true)
  public void delete(UUID id) {

    Channel channel = channelRepository.findById(id)
                                       .orElseThrow(() -> new ChannelNotFoundException(id));

    if (channel != null) {
      channelRepository.delete(channel);

      log.info("Channel deleted: {}", channel);
    }
  }

  @Transactional
  @Override
  public void deleteAll() {
    channelRepository.deleteAll();
  }
}
