package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service("readStatusService")
@Validated
public class BasicReadStatusService implements ReadStatusService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public ReadStatus create(@Valid ReadStatusCreateRequest readStatusCreateRequest) {
    if (userRepository.findById(readStatusCreateRequest.userId()).isEmpty()
        || channelRepository.findById(readStatusCreateRequest.channelId()).isEmpty()) {
      throw new NoSuchElementException("create : 관련된 채널 또는 유저가 존재하지 않습니다.");
    }
    if (readStatusRepository.findByUserId(readStatusCreateRequest.userId()).stream()
        .anyMatch(status -> status.getChannelId().equals(readStatusCreateRequest.channelId()))) {
      throw new IllegalArgumentException("create : 이미 존재하는 ReadStatus 입니다.");
    }

    ReadStatus readStatus = new ReadStatus(readStatusCreateRequest.userId(),
        readStatusCreateRequest.channelId());
    return readStatusRepository.save(readStatus);
  }

  @Override
  public ReadStatus findById(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("findById : ReadStatus를 찾을 수 없습니다."));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("findAllByUserId : 유저를 찾을 수 없습니다.");
    }
    return readStatusRepository.findByUserId(userId);
  }

  @Override
  public ReadStatus update(@Valid ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusUpdateRequest.id())
        .orElseThrow(() -> new NoSuchElementException("update : ReadStatus를 찾을 수 없습니다."));
    readStatus.update(readStatusUpdateRequest.read());

    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID id) {
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("delete : ReadStatus를 찾을 수 없습니다.");
    }
    readStatusRepository.deleteById(id);
  }
}
