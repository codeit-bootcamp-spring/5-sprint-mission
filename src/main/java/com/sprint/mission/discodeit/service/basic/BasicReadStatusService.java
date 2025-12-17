package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.*;

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
    log.info("읽음 상태 생성 요청: userId={}, channelId={}", userId, channelId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> {
              log.error("읽음 상태 생성 실패 - 유저 없음: userId={}", userId);
              return new UserNotFoundException();
            });

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.error("읽음 상태 생성 실패 - 채널 없음: channelId={}", channelId);
              return new ChannelNotFoundException();
            });

    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      log.warn("읽음 상태 생성 실패 - 이미 존재: userId={}, channelId={}", userId, channelId);
      throw new ReadStatusAlreadyExistsException();
    }

    ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
    readStatusRepository.save(readStatus);
    log.info("읽음 상태 생성 성공: readStatusId={}", readStatus.getId());

    return readStatusMapper.toDto(readStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.info("읽음 상태 조회 요청: readStatusId={}", readStatusId);
    return readStatusRepository.findById(readStatusId)
            .map(readStatusMapper::toDto)
            .orElseThrow(() -> {
              log.error("읽음 상태 조회 실패 - 없음: readStatusId={}", readStatusId);
              return new ReadStatusNotFoundException();
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.info("유저별 읽음 상태 전체 조회 요청: userId={}", userId);
    List<ReadStatusDto> list = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    log.info("유저별 읽음 상태 조회 완료: userId={}, count={}", userId, list.size());
    return list;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.info("읽음 상태 수정 요청: readStatusId={}", readStatusId);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> {
              log.error("읽음 상태 수정 실패 - 없음: readStatusId={}", readStatusId);
              return new ReadStatusNotFoundException();
            });

    readStatus.update(request.newLastReadAt());
    readStatus.updateNotification(request.newNotificationEnabled());
    log.info("읽음 상태 수정 성공: readStatusId={}", readStatusId);

    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.info("읽음 상태 삭제 요청: readStatusId={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      log.error("읽음 상태 삭제 실패 - 없음: readStatusId={}", readStatusId);
      throw new ReadStatusNotFoundException();
    }
    readStatusRepository.deleteById(readStatusId);
    log.info("읽음 상태 삭제 성공: readStatusId={}", readStatusId);
  }
}