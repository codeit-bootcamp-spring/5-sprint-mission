package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

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
  }
}
