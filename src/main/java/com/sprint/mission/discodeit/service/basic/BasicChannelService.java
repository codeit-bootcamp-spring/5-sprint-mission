package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelParameterException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateForbiddenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Caching(evict = {
      @CacheEvict(value = "channels", allEntries = true)
  })
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  @Transactional
  public ChannelDTO createPublicChannel(PublicChannelCreateRequest request) {
    // Public은 생성시 기존 로직을 유지.
    Channel channel = Channel.builder()
        .type(ChannelType.PUBLIC)
        .name(request.name())
        .description(request.description())
        .build();

    log.info("생성할 Public 채널 이름={}", channel.getName());

    try {
      Channel save = channelRepository.save(channel);
      log.debug("Public 채널 생성 완료 이름={}", save.getId());
      return channelMapper.toDto(save);
    } catch (Exception e) {
      log.error("Public 채널 생성 실패 이름={}", channel.getName(), e);
      throw InvalidChannelParameterException.withMessage(e.getMessage());
    }
  }

  @Caching(evict = {
      @CacheEvict(value = "channels", allEntries = true)
  })
  @Override
  @Transactional
  public ChannelDTO createPrivateChannel(PrivateChannelCreateRequest request) {
    // Private 채널 생성
    Channel channel = Channel.builder()
        .type(ChannelType.PRIVATE)
        .build();

    log.info("생성할 Private 채널 이름={}", channel.getName());
    try {
      Channel save = channelRepository.save(channel);

      request.participantIds().stream()
          .map(userId -> userRepository.findById(userId)
              .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다.")))
          .map(user -> ReadStatus.builder()
              .user(user)
              .channel(save)
              .lastReadAt(Instant.now())
              .notificationEnabled(true)
              .build())
          .forEach(readStatusRepository::save);

      log.debug("Private 채널 생성 완료 이름={}", save.getId());
      ChannelDTO channelDTO = channelMapper.toDto(save);
      applicationEventPublisher.publishEvent(
          new ChannelUpdatedEvent("channels.created", channelDTO));
      return channelDTO;
    } catch (Exception e) {
      log.error("Private 채널 생성 실패 이름={}", channel.getName(), e);
      throw InvalidChannelParameterException.withMessage(e.getMessage());
    }
  }

  @Cacheable(value = "channels", key = "#userId")
  @Override
  @Transactional(readOnly = true)
  public List<ChannelDTO> findAllByUserId(UUID userId) {
    // 호환성 체크
    userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    // 1. Private 채널은 User가 참여한 채널만 조회하기 위한 ChannelId를 List화 시킴.
    List<Channel> subscribeChannels = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel -> channel.getType() == ChannelType.PUBLIC
            || (channel.getType() == ChannelType.PRIVATE && subscribeChannels.contains(channel)))
        .map(channelMapper::toDto)
        .toList();
  }

  @Caching(evict = {
      @CacheEvict(value = "channels", allEntries = true)
  })
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  @Transactional
  public ChannelDTO updateChannel(UUID channelId, ChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId).orElseThrow(() -> {
      log.warn("존재하지 않는 채널 업데이트 시도 이름={}", request.newName());
      return new ChannelNotFoundException();
    });

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateForbiddenException();
    }

    log.info("업데이트할 Public 채널 이름={}", channel.getName());

    Channel updateChannel = channel.toBuilder()
        .name(request.newName() != null ? request.newName() : channel.getName())
        .description(
            request.newDescription() != null ? request.newDescription() : channel.getDescription())
        .build();

    try {
      Channel save = channelRepository.save(updateChannel);
      log.debug("업데이트된 Public 채널 이름={}", save.getId());
      ChannelDTO channelDTO = channelMapper.toDto(save);
      applicationEventPublisher.publishEvent(
          new ChannelUpdatedEvent("channels.updated", channelDTO));
      return channelDTO;
    } catch (Exception e) {
      log.error("Public 채널 업데이트 실패 이름={}", channel.getName(), e);
      throw InvalidChannelParameterException.withMessage(e.getMessage());
    }
  }

  @Caching(evict = {
      @CacheEvict(value = "channels", allEntries = true)
  })
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Override
  @Transactional
  public void deleteChannel(UUID channelId) {

    // 관련 도메인 같이 삭제 Channel, Message, ReadStatus
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(ChannelNotFoundException::new);

    try {
      channelRepository.deleteById(channelId);
      messageRepository.deleteByChannelId(channelId);
      readStatusRepository.deleteByChannelId(channelId);
      applicationEventPublisher.publishEvent(
          new ChannelUpdatedEvent("channels.deleted", channelMapper.toDto(channel)));
      log.debug("채널 삭제 완료 아이디 ={}", channelId);
    } catch (Exception e) {
      log.error("채널 삭제 실패", e);
      throw InvalidChannelParameterException.withMessage(e.getMessage());
    }
  }
}
