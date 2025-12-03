package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 요청: name={}, description={}", request.name(), request.description());

    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.save(channel);

    log.info("공개 채널 생성 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청: participantCount={}", request.participantIds().size());

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("비공개 채널 생성 성공: channelId={}, participants={}",
            channel.getId(), readStatuses.size());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    log.info("채널 단건 조회 요청: channelId={}", channelId);
    return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> {
              log.error("채널 조회 실패 - 없음: channelId={}", channelId);
              return new ChannelNotFoundException();
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.info("유저 구독 채널 목록 조회 요청: userId={}", userId);

    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

    List<ChannelDto> results = channelRepository
            .findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds).stream()
            .map(channelMapper::toDto)
            .toList();

    log.info("유저 구독 채널 조회 완료: userId={}, count={}", userId, results.size());
    return results;
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("공개 채널 수정 요청: channelId={}", channelId);

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.error("채널 수정 실패 - 없음: channelId={}", channelId);
              return new ChannelNotFoundException();
            });

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("비공개 채널 수정 시도 차단: channelId={}", channelId);
      throw new PrivateChannelUpdateException();
    }

    channel.update(request.newName(), request.newDescription());
    log.info("공개 채널 수정 성공: channelId={}", channelId);
    return channelMapper.toDto(channel);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.info("채널 삭제 요청: channelId={}", channelId);

    if (!channelRepository.existsById(channelId)) {
      log.error("채널 삭제 실패 - 없음: channelId={}", channelId);
      throw new ChannelNotFoundException();
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    log.info("채널 삭제 성공: channelId={}", channelId);
  }
}