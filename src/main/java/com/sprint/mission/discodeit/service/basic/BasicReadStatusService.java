package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
=======
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
<<<<<<< HEAD
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
=======
import java.util.UUID;
import lombok.Locked.Read;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
<<<<<<< HEAD

  @Override
  public ReadStatusResponseDto create(ReadStatusCreateRequest request) {
    if (!userRepository.existsById(request.userId())) {
      throw new NoSuchElementException("User not found with id " + request.userId());
    }
    if (!channelRepository.existsById(request.channelId())) {
      throw new NoSuchElementException("Channel not found with id " + request.channelId());
    }

    // userId와 channelId로 기존 ReadStatus를 찾습니다.
    Optional<ReadStatus> existingReadStatus = readStatusRepository.findByUserIdAndChannelId(
        request.userId(), request.channelId());

    ReadStatus readStatusToSave;

    if (existingReadStatus.isPresent()) {
      // 이미 존재하면 해당 객체를 가져와서 업데이트 시간을 갱신합니다.
      readStatusToSave = existingReadStatus.get();
      readStatusToSave.setLastReadAt(Instant.now());
    } else {
      // 존재하지 않으면 새로운 ReadStatus 객체를 생성합니다.
      readStatusToSave = new ReadStatus(
          request.userId(),
          request.channelId(),
          Instant.now()
      );
    }

    // save() 메서드는 JPA가 엔티티 ID를 보고 INSERT 또는 UPDATE를 결정합니다.
    ReadStatus saved = readStatusRepository.save(readStatusToSave);

    return new ReadStatusResponseDto(
        saved.getId(),
        saved.getUserId(),
        saved.getChannelId(),
        saved.getCreatedAt(),
        saved.getUpdatedAt(),
        saved.getLastReadAt()
    );
  }

  @Override
  public ReadStatusResponseDto find(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + id));

    return new ReadStatusResponseDto(
        readStatus.getId(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getLastReadAt()
    );
  }

  @Override
  public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User not found with id " + userId);
    }
    List<ReadStatus> list = readStatusRepository.findByUserId(userId);
    return list.stream()
        .map(status -> new ReadStatusResponseDto(
            status.getId(),
            status.getUserId(),
            status.getChannelId(),
            status.getCreatedAt(),
            status.getUpdatedAt(),
            status.getLastReadAt()
        ))
        .toList();
  }

  @Override
  public List<ReadStatusResponseDto> findAllByChannelId(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("User not found with id " + channelId);
    }
    List<ReadStatus> list = readStatusRepository.findByChannelId(channelId);
    return list.stream()
        .map(status -> new ReadStatusResponseDto(
            status.getId(),
            status.getUserId(),
            status.getChannelId(),
            status.getCreatedAt(),
            status.getUpdatedAt(),
            status.getLastReadAt()
        ))
        .toList();
  }

  @Override
  public ReadStatusResponseDto update(UUID id, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + id));

    readStatus.update(newLastReadAt);
    ReadStatus updated = readStatusRepository.save(readStatus);

    return new ReadStatusResponseDto(
        updated.getId(),
        updated.getUserId(),
        updated.getChannelId(),
        updated.getCreatedAt(),
        updated.getUpdatedAt(),
        updated.getLastReadAt()
    );
  }

  @Override
  public void delete(UUID id) {
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("ReadStatus not found with id " + id);
    }
    readStatusRepository.deleteById(id);
=======
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
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
  }
}
