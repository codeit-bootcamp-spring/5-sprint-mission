// src/main/java/com/sprint/mission/discodeit/service/impl/ReadStatusServiceImpl.java

package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusDto dto) { // DTO를 받아서 DTO를 반환
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));
    Channel channel = channelRepository.findById(dto.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없음"));

    // 매퍼를 사용해 엔티티로 변환
    ReadStatus readStatus = readStatusMapper.toEntity(dto, user, channel);

    // 저장 후 DTO로 변환하여 반환
    ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
    return readStatusMapper.toDto(savedReadStatus);
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusDto dto) { // DTO를 받아서 DTO를 반환
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException("해당 읽음상태 없음"));

    // 매퍼를 사용해 엔티티 값 업데이트
    readStatusMapper.updateEntityFromDto(readStatus, dto);

    // DTO로 변환하여 반환
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public List<ReadStatusDto> findAllByUserId(UUID userId) { //  DTO 리스트 반환
    List<ReadStatus> readStatuses = readStatusRepository.findByUser_Id(userId);

    // 매퍼를 사용해 DTO 리스트로 변환
    return readStatusMapper.toDtoList(readStatuses);
  }

  @Override
  @Transactional
  public void deleteByChannelId(UUID channelId) {
    readStatusRepository.deleteByChannel_Id(channelId);
  }

  @Override
  @Transactional
  public ReadStatusDto findById(UUID id) { // DTO 반환
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 읽음 상태 없음"));

    // DTO로 변환하여 반환
    return readStatusMapper.toDto(readStatus);
  }
}