package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository; // ⬅ 추가

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    return channelRepository.save(channel);
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel createdChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")))
        .map(user -> new ReadStatus(user, createdChannel, Instant.now()))
        .forEach(readStatusRepository::save);

    return createdChannel;
  }

  @Override @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    Set<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(rs -> rs.getChannel().getId())
        .collect(Collectors.toSet());

    return channelRepository.findAll().stream()
        .filter(ch -> ch.getType() == ChannelType.PUBLIC || mySubscribedChannelIds.contains(ch.getId()))
        .map(this::toDto)
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType() == ChannelType.PRIVATE) throw new IllegalArgumentException("Private channel cannot be updated");
    channel.update(request.newName(), request.newDescription());
    return channelRepository.save(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());
    channelRepository.deleteById(channelId);
  }

  // ─────────────────────────────────────────────
  // 여기부터 수정: participants 를 List<UserDto> 로 생성
  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = messageRepository.findAllByChannel_Id(channel.getId()).stream()
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .map(Message::getCreatedAt)
        .findFirst()
        .orElse(Instant.MIN);

    List<UserDto> participants = Collections.emptyList();
    if (channel.getType() == ChannelType.PRIVATE) {
      participants = readStatusRepository.findAllByChannel_Id(channel.getId()).stream()
          .map(ReadStatus::getUser)     // User 엔티티
          .map(this::toUserDto)         // User → UserDto
          .toList();
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,      // ✅ List<UserDto>
        lastMessageAt
    );
  }

  private UserDto toUserDto(User user) {
    // profile → BinaryContentDto
    BinaryContent profile = user.getProfile();
    BinaryContentDto profileDto = null;
    if (profile != null) {
      profileDto = new BinaryContentDto(
          profile.getId(),
          profile.getFileName(),
          profile.getSize(),
          profile.getContentType(),
          profile.getBytes()
      );
    }

    // online 계산 (최근 5분 이내 활동)
    Boolean online = userStatusRepository.findByUser_Id(user.getId())
        .map(us -> us.getLastActiveAt() != null
            && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online
    );
  }
}
