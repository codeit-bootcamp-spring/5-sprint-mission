package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ReadStatusDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.Channel;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;

  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto.Detail create(CreateCommand request) {

    User user = userRepository.findById(request.getUserId())
                              .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
    Channel channel = channelRepository.findById(request.getChannelId())
                                       .orElseThrow(() -> new ChannelNotFoundException(
                                           request.getChannelId()));

    if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId())
                            .isPresent()) {
      throw new ReadStatusAlreadyExistsException(request.getUserId(), request.getChannelId());
    }

    ReadStatus readStatus = readStatusMapper.toEntity(request, user, channel);
    readStatusRepository.save(readStatus);

    return readStatusMapper.toDetail(readStatus);
  }

  @Override
  public ReadStatusDto.Detail find(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
                                                .orElseThrow(
                                                    () -> new ReadStatusNotFoundException(id));

    return readStatusMapper.toDetail(readStatus);
  }

  @Override
  public List<ReadStatusDto.Detail> findAllByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findByUserId(userId);

    return readStatuses.stream()
                       .map(readStatusMapper::toDetail)
                       .toList();
  }

  @Override
  @Transactional
  public void delete(UUID id) {

    ReadStatus readStatus = readStatusRepository.findById(id)
                                                .orElseThrow(
                                                    () -> new ReadStatusNotFoundException(id));

    readStatusRepository.delete(readStatus);
  }

  @Override
  @Transactional
  public void deleteAll() {
    readStatusRepository.deleteAll();
  }

  @Override
  @Transactional
  public ReadStatusDto.Detail update(UpdateCommand command) {

    ReadStatus readStatus = readStatusRepository.findById(command.getId())
                                                .orElseThrow(() -> new ReadStatusNotFoundException(
                                                    command.getId()));

    readStatus.update(command.getLastReadAt(), command.getNotificationEnabled());

    return readStatusMapper.toDetail(readStatus);
  }
}
