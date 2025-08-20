package com.sprint.mission.discodeit.service.readstatus;

import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadStatusService {

  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    userRepository.getOrThrow(userId);
    return readStatusRepository.findAllByUserId(userId).stream().map(ReadStatusResponse::from)
        .toList();
  }

  @Transactional
  public ReadStatusResponse create(ReadStatusCreateRequest req) {
    userRepository.getOrThrow(req.userId());
    channelRepository.getOrThrow(req.channelId());

    readStatusRepository.findByUserIdAndChannelId(req.userId(), req.channelId())
        .ifPresent(rs -> {
          throw new IllegalStateException(
              "ReadStatus with userId %s and channelId %s already exists.".formatted(req.userId(),
                  req.channelId()));
        });

    return ReadStatusResponse.from(readStatusRepository.save(
        new ReadStatus(req.userId(), req.channelId(), req.lastReadAt())));
  }

  @Transactional
  public void update(UUID id, ReadStatusUpdateRequest req) {
    ReadStatus rs = readStatusRepository.getOrThrow(id);
    readStatusRepository.save(rs);
  }

  public ReadStatusResponse findByUserAndChannel(UUID userId, UUID channelId) {
    userRepository.getOrThrow(userId);
    channelRepository.getOrThrow(channelId);
    ReadStatus rs = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 정보가 없습니다"));
    return ReadStatusResponse.from(rs);
  }
}
