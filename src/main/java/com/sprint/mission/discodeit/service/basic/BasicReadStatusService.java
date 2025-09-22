package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;                 // 🔹 추가
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;       // 🔹 추가
import com.sprint.mission.discodeit.exception.read.ReadStatusAlreadyExistsException;  // 🔹 추가
import com.sprint.mission.discodeit.exception.read.ReadStatusNotFoundException;       // 🔹 추가
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();
    log.info("[READ][CREATE] userId={} channelId={}", userId, channelId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[READ][CREATE] user not found userId={}", userId);
          return new UserNotFoundException(userId);
        });
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("[READ][CREATE] channel not found channelId={}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      log.warn("[READ][CREATE] duplicate userId={} channelId={}", userId, channelId);
      throw new ReadStatusAlreadyExistsException(userId, channelId);
    }

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    readStatusRepository.save(readStatus);

    log.info("[READ][CREATE][DONE] id={} userId={} channelId={}",
        readStatus.getId(), userId, channelId);
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("[READ][FIND] id={}", readStatusId);
    return readStatusRepository.findById(readStatusId)
        .map(rs -> {
          log.info("[READ][FIND][DONE] id={}", readStatusId);
          return readStatusMapper.toDto(rs);
        })
        .orElseThrow(() -> {
          log.warn("[READ][FIND] not-found id={}", readStatusId);
          return new ReadStatusNotFoundException(readStatusId);
        });
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("[READ][FIND_ALL_BY_USER] userId={}", userId);
    List<ReadStatusDto> list = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
    log.info("[READ][FIND_ALL_BY_USER][DONE] userId={} total={}", userId, list.size());
    return list;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    log.info("[READ][UPDATE] id={} newLastReadAt={}", readStatusId, newLastReadAt);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("[READ][UPDATE] not-found id={}", readStatusId);
          return new ReadStatusNotFoundException(readStatusId);
        });

    readStatus.update(newLastReadAt);
    log.info("[READ][UPDATE][DONE] id={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.info("[READ][DELETE] id={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("[READ][DELETE] not-found id={}", readStatusId);
      throw new ReadStatusNotFoundException(readStatusId);
    }
    readStatusRepository.deleteById(readStatusId);
    log.info("[READ][DELETE][DONE] id={}", readStatusId);
  }
}
