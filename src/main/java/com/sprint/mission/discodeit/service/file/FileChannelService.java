package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary // 같은 타입 서비스가 여러 개면 우선 적용
@RequiredArgsConstructor // final 필드 기반 생성자 자동 생성 (this 안써도 됨)
public class FileChannelService implements ChannelService {

  private final ChannelRepository channelRepository; // 채널 저장
  private final ReadStatusRepository readStatusRepository; //채널 생성후 읽음상태 저장할수있도록

  @Override
  public void create(Channel channel) {
    if (channel == null) {
      throw new IllegalArgumentException("채널 정보가 없습니다");
    }
    if (channel.getTitle() == null || channel.getTitle().isEmpty()) {
      throw new IllegalArgumentException("채널 이름이 빈칸입니다.");
    }
    if (channel.getDescription() == null || channel.getDescription().isEmpty()) {
      throw new IllegalArgumentException("채널 설명이 빈칸입니다.");
    }
    channelRepository.save(channel);
  }

  @Override
  public Channel findById(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("조회할 채널의 ID가 null입니다.");
    }
    Channel original = channelRepository.findById(channelId);
    if (original == null) {
      throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
    }
    return new Channel(original); // 복사본 리턴
  }

  @Override
  public List<Channel> findAll(UUID userId) {
    return channelRepository.findAll();
  }

  @Override
  public void update(UUID channelId, Channel channel) {
    if (channel == null) {
      throw new IllegalArgumentException("채널이 null값입니다.");
    }
    if (channel.getTitle() == null || channel.getTitle().isEmpty()) {
      throw new IllegalArgumentException("채널 이름이 빈칸입니다.");
    }
    if (channel.getDescription() == null || channel.getDescription().isEmpty()) {
      throw new IllegalArgumentException("채널 설명이 빈칸입니다.");
    }
    channelRepository.update(channel);
  }

  @Override
  public void delete(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("채널 ID가 null값입니다.");
    }
    if (channelRepository.findById(channelId) == null) {
      throw new IllegalArgumentException("삭제할 채널이 존재하지 않습니다.");
    }
    channelRepository.delete(channelId);
  }

  @Override
  public void createPrivateChannel(PrivateChannelCreateRequest request) {
    UUID channelId = UUID.randomUUID(); // 미리 생성해 고정

    //1. DTO에서 Entity로 변환
    Channel privateChannel = request.toEntityWithId(channelId);

    //2. 채널 먼저 저장
    channelRepository.save(privateChannel);

    //3. 참여 유저 수만큼 반복하며 ReadStatus 생성
    for (String memberId : request.getMembersId()) {
      ReadStatus readStatus = new ReadStatus(
          UUID.randomUUID(),                  // 읽음 기록의 고유 ID
          channelId,            // 어떤 채널의 기록인지
          UUID.fromString(memberId),          // 어떤 유저가 읽은 건지
          Instant.now()                       // 생성 시간
      );
      readStatusRepository.save(readStatus);  // 저장

    }
  }

  @Override
  public void createPublicChannel(PublicChannelCreateRequest request) {
    //📌 DTO에서 직접 Entity로 변환
    channelRepository.save(request.toEntity());
  }


}
