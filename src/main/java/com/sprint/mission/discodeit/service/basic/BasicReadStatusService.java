package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.Locked.Read;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    log.info("[READ_STATUS][CREATE] userId={}, channelId={}", request.userId(),
        request.channelId());
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      log.warn("[READ_STATUS][CREATE][FAILED] userId={}, channelId={} already exists", userId,
          channelId);
      throw new ReadStatusAlreadyExistsException(userId, channelId);
    }

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    readStatusRepository.save(readStatus);
    ReadStatusDto dto = readStatusMapper.toDto(readStatus);
    log.debug("[READ_STATUS][CREATE][DONE] id={}", dto.id());
    return dto;
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.info("[READ_STATUS][FIND] id={}", readStatusId);
    ReadStatusDto dto = readStatusRepository.findById(readStatusId).map(readStatusMapper::toDto).orElseThrow(
        () -> new ReadStatusNotFoundException(readStatusId));

    log.debug("[READ_STATUS][FIND][DONE] id={}", dto.id());
    return dto;
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.info("[READ_STATUS][FIND_ALL_BY_USER_ID] userId={}", userId);
    List<ReadStatusDto> readStatusDtos = readStatusRepository.findAllByUserId(userId).stream().map(readStatusMapper::toDto)
        .toList();
    log.debug("[READ_STATUS][FIND_ALL_BY_USER_ID][DONE] readStatusDtos={}", readStatusDtos);
    return readStatusDtos;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.info("[READ_STATUS][UPDATE] id={}, newLastReadAt={}", readStatusId, request.newLastReadAt());
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(
        () -> new ReadStatusNotFoundException(readStatusId));
    readStatus.update(newLastReadAt);
    ReadStatusDto dto = readStatusMapper.toDto(readStatus);
    log.debug("[READ_STATUS][UPDATE][DONE] id={}", dto.id());
    return dto;
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.warn("[READ_STATUS][DELETE] id={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
    log.debug("[READ_STATUS][DELETE][DONE] id={}", readStatusId);
  }
}
