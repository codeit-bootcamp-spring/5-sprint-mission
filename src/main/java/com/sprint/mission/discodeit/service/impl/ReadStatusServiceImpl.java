package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
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

  //읽음상태 생성
  @Override
  @Transactional
  public ReadStatus create(ReadStatusCreateRequest request) {

    //1.User, Channel 엔티티 조회 (영속성 컨텍스트에 올리기)
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));
    Channel channel = channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없음"));

    //2. ReadStatus 엔티티 생성
    ReadStatus readStatus = new ReadStatus(user, channel, request.getLastReadAt());

    //3. 저장(영속 상태 올림)
    return readStatusRepository.save(readStatus);


  }

  /* 읽음상태 업데이트
   * setter 호출, 트랜잭션 끝나면 자동 update
   */
  @Override
  @Transactional
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException("해당 읽음상태 없음"));

    if (request.getNewLastReadAt() != null) {
      readStatus.update(request.getNewLastReadAt());
    }
    return readStatus;
  }

  /*특정 유저 읽음상태 전부 조회
   * 예: 어떤 채널에서 읽지 않은 메시지가 몇 개인지
   */
  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findByUser_Id(userId);
  }

  /*채널 삭제시, 해당 채널의 모든 읽음기록 삭제
   */
  @Override
  @Transactional
  public void deleteByChannelId(UUID channelId) {
    readStatusRepository.deleteByChannel_Id(channelId);
  }

  /*읽음상태 단건 조회
   */
  @Override
  @Transactional
  public ReadStatus findById(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 읽음 상태 없음"));
  }
}
