package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDto> findByUser(UUID userId) {
    return readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatusMapper::toDto)
        .toList(); // 없으면 [] 반환
  }

  @Override
  public ReadStatusDto markRead(UUID userId, UUID channelId, ReadStatusUpdateRequest req) {
    ReadStatus rs = readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)
        .orElseGet(() -> createReadStatus(userId, channelId));

    Instant now = Instant.now();
    Instant requested = (req != null && req.newLastReadAt() != null) ? req.newLastReadAt() : now;

    // 과거로의 되돌림 방지: 단조 증가
    if (rs.getLastReadAt() == null || requested.isAfter(rs.getLastReadAt())) {
      // 엔티티에 update(Instant) 메서드가 이미 있음(기존 코드 기준)
      rs.update(requested);
    }

    return readStatusMapper.toDto(rs); // 변경감지로 커밋 시 반영
  }

  private ReadStatus createReadStatus(UUID userId, UUID channelId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Channel not found"));

    // 새 행은 EPOCH에서 시작(규칙에 맞게 now로 바꿔도 무방)
    ReadStatus rs = new ReadStatus(user, channel, Instant.EPOCH);
    return readStatusRepository.save(rs);
  }
}
