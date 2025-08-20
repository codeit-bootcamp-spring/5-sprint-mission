package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.Create;
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

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public ReadStatusDto.Detail create(Create request) {

    userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("Not Found User"));
    channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("Not Found Channel"));

    if (readStatusRepository.findAllByUserId(request.getUserId()).stream()
        .anyMatch(rs -> rs.getChannelId().equals(request.getChannelId()))) {
      throw new IllegalArgumentException("Already Registered Read Status");
    }

    ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId());
    readStatusRepository.save(readStatus);

    return ReadStatusDto.Detail.builder().id(readStatus.getId())
        .channelId(readStatus.getChannelId()).userId(readStatus.getUserId())
        .lastReadAt(readStatus.getLastReadAt()).build();
  }

  @Override
  public ReadStatusDto.Detail find(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Not Found Read Status"));

    return ReadStatusDto.Detail.builder().id(readStatus.getId())
        .channelId(readStatus.getChannelId()).userId(readStatus.getUserId())
        .lastReadAt(readStatus.getLastReadAt()).build();
  }

  @Override
  public List<ReadStatusDto.Detail> findAllByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

    return readStatuses.stream().map(
        rs -> ReadStatusDto.Detail.builder().id(rs.getId()).channelId(rs.getChannelId())
            .userId(rs.getUserId()).lastReadAt(rs.getLastReadAt()).build()).toList();
  }

  @Override
  public void delete(UUID id) {
    readStatusRepository.delete(id);
  }

  @Override
  public void deleteAll() {
    readStatusRepository.deleteAll();
  }

  @Override
  public ReadStatusDto.Detail update(UUID id) {

    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Not Found Read Status"));

    readStatus.update();
    readStatusRepository.save(readStatus);

    return ReadStatusDto.Detail.builder().id(readStatus.getId())
        .channelId(readStatus.getChannelId()).userId(readStatus.getUserId())
        .lastReadAt(readStatus.getLastReadAt()).build();
  }
}
