package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  private final MessageMapper messageMapper;
  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;


  @Transactional
  @Override
  public ChannelDto.Detail create(CreateCommand create) {

    Channel channel = Channel.builder()
                             .type(create.getType())
                             .description(create.getType()
                                                .equals(ChannelType.PRIVATE) ? ""
                                 : create.getDescription())
                             .name(create.getType()
                                         .equals(ChannelType.PRIVATE) ? "" : create.getName())
                             .build();

    channelRepository.save(channel);

    if (create.getType()
              .equals(ChannelType.PRIVATE)) {

      List<User> users = userRepository.findAllById(create.getParticipantIds());
      List<ReadStatus> readStatuses = users.stream()
                                           .map(user -> ReadStatus.builder()
                                                                  .channel(channel)
                                                                  .user(user)
                                                                  .build())
                                           .toList();
      readStatusRepository.saveAll(readStatuses);

      channel.getReadStatuses()
             .addAll(readStatuses);
    }

    return channelMapper.toDetail(channel);
  }

  public ChannelDto.Detail findById(UUID id) {

    Channel channel = channelRepository.findById(id)
                                       .orElseThrow(() -> new RuntimeException("Not Found"));

    return channelMapper.toDetail(channel);
  }

  public List<ChannelDto.Detail> findAll() {

    List<Channel> channels = channelRepository.findAll();

    return channels.stream()
                   .map(channelMapper::toDetail)
                   .toList();
  }

  public List<ChannelDto.Detail> findAllByUserId(UUID userId) {

    List<Channel> channels = new java.util.ArrayList<>();

    channels.addAll(channelRepository.findByType(ChannelType.PUBLIC));
    channels.addAll(readStatusRepository.findByUserId(userId)
                                        .stream()
                                        .map(ReadStatus::getChannel)
                                        .toList());

    return channels.stream()
                   .distinct()
                   .map(channelMapper::toDetail)
                   .toList();
  }

  @Transactional
  @Override
  public ChannelDto.Detail update(UpdateCommand update) {

    Channel channel = channelRepository.findById(update.getId())
                                       .orElse(null);

    if (channel == null || channel.getType()
                                  .equals(ChannelType.PRIVATE)) {
      throw new RuntimeException("Not Found");
    }

    if (update.getName() != null && update.getDescription() != null) {
      channel.update(update.getName(), update.getDescription());
    }

    if (update.getParticipantIds() != null) {
      update.getParticipantIds()
            .forEach(userId -> {
              if (channel.getReadStatuses()
                         .stream()
                         .anyMatch(rs -> rs.getUser()
                                           .getId()
                                           .equals(userId))) {
                return;
              }

              // TODO ReadStatus 만들어주기
            });
    }

    channelRepository.save(channel);

    return channelMapper.toDetail(channel);
  }

  @Transactional
  @Override
  public void delete(UUID id) {

    Channel channel = channelRepository.findById(id)
                                       .orElseThrow(() -> new RuntimeException("Not Found"));

    if (channel != null) {
      channelRepository.delete(channel);

      // TODO 제약조건 걸었으니 안지워도 되나? 나중에 확인
      messageRepository.deleteAll(channel.getMessages());
      readStatusRepository.deleteAll(channel.getReadStatuses());
    }
  }

  @Transactional
  @Override
  public void deleteAll() {
    channelRepository.deleteAll();
  }
}
