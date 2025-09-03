package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
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
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service("readStatusService")
@Validated
public class BasicReadStatusService implements ReadStatusService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(@Valid ReadStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException("User not found [" + request.userId() + "]"));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(
            () -> new NoSuchElementException("Channel not found [" + request.channelId() + "]"));

    if (readStatusRepository.findAllByUserId(request.userId()).stream()
        .anyMatch(status -> status.getChannel().getId().equals(request.channelId()))) {
      throw new IllegalArgumentException("create : 이미 존재하는 ReadStatus 입니다.");
    }

    ReadStatus readStatus = new ReadStatus(channel.getCreatedAt(), user, channel);
    return readStatusMapper.toDto(readStatusRepository.save(readStatus));
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto findById(UUID id) {
    return readStatusMapper.toDto(
        readStatusRepository.findById(id)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "findById : ReadStatus를 찾을 수 없습니다. [" + id + "]")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("findAllByUserId : 유저를 찾을 수 없습니다. [" + userId + "]");
    }
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId,
      @Valid ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException(
            "update : ReadStatus를 찾을 수 없습니다. [" + readStatusId + "]"));
    readStatus.update(readStatusUpdateRequest.newLastReadAt());

    return readStatusMapper.toDto(readStatusRepository.save(readStatus));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("delete : ReadStatus를 찾을 수 없습니다. [" + id + "]");
    }
    readStatusRepository.deleteById(id);
  }
}
