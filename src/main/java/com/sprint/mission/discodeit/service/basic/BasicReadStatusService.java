package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Override
  public void deleteReadStatus(UUID id) {
    readStatusRepository.deleteById(id);
  }

  @Override
  public void deleteAllReadStatus() {
    readStatusRepository.deleteAll();
  }

  @Override
  public ReadStatus getReadStatus(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found"));
  }

  @Override
  public List<ReadStatus> getAllReadStatusByUserId(UUID userId) {
    List<ReadStatus> allByUserId = readStatusRepository.findAllByUserId(userId);
    if (allByUserId.isEmpty()) {
      throw new IllegalArgumentException("ReadStatus not found");
    }

    return allByUserId;
  }


  @Override
  public ReadStatus addReadStatus(AddReadStatusRequest addReadStatusRequest) {
    channelRepository.findById(addReadStatusRequest.channelId())
        .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
    userRepository.findById(addReadStatusRequest.userId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    ReadStatus readStatus = new ReadStatus(addReadStatusRequest.userId(),
        addReadStatusRequest.channelId());
    return readStatusRepository.save(readStatus)
        .orElseThrow(() -> new RuntimeException("Failed to save ReadStatus"));
  }

  @Override
  public ReadStatus updateReadStatus(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException("Update: ReadStatus not found"));
    readStatus.updateLastReadTime();
    return readStatusRepository.save(readStatus)
        .orElseThrow(() -> new RuntimeException("Failed to update ReadStatus"));
  }
}
