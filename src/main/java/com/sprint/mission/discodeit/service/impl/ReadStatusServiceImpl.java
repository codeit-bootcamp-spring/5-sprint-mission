package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusDto dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new UserNotFoundException());
    Channel channel = channelRepository.findById(dto.getChannelId())
        .orElseThrow(() -> new ChannelNotFoundException());

    // 매퍼를 사용해 엔티티로 변환
    ReadStatus readStatus = readStatusMapper.toEntity(dto, user, channel);

    // 저장 후 DTO로 변환하여 반환
    ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
    log.info("읽음상태 생성 완료: readStatusId={}", savedReadStatus.getId());
    return readStatusMapper.toDto(savedReadStatus);
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusDto dto) { // DTO를 받아서 DTO를 반환
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException());

    // 매퍼를 사용해 엔티티 값 업데이트
    readStatusMapper.updateEntityFromDto(readStatus, dto);

    // DTO로 변환하여 반환
    log.info("읽음상태 업데이트 완료: readStatusId={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findByUser_Id(userId);
    log.info("내 모든 채팅방 읽음상태 조회 완료: userId={}, 개수={}", userId, readStatuses.size());

    // 매퍼를 사용해 DTO 리스트로 변환
    return readStatusMapper.toDtoList(readStatuses);
  }

  @Override
  @Transactional
  public void deleteByChannelId(UUID channelId) {
    readStatusRepository.deleteByChannel_Id(channelId);
    log.info("채널의 모든 읽음상태 삭제 완료: channelId={}", channelId);
  }

  @Override
  @Transactional
  public ReadStatusDto findById(UUID id) { // DTO 반환
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new ReadStatusNotFoundException());
    log.info("읽음상태 단건조회 성공: id={}", id);

    // DTO로 변환하여 반환
    return readStatusMapper.toDto(readStatus);
  }
}