package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

  private ChannelRepository channelRepository;

  //채널 생성
  @Override
  @Transactional
  public void create(Channel channel) {
    channelRepository.save(channel);
  }

  //채널 단건 조회
  @Override
  @Transactional
  public Channel findById(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없음"));
  }

  //채널 리스트 조회
  @Override
  @Transactional
  public List<Channel> findAll() {
    return channelRepository.findAll();
  }

  /* 채널 정보 수정
   * save 호출X, Dirty Checking
   * 메서드 끝날때 트랜잭션 commit됨
   * */
  @Override
  @Transactional
  public void update(UUID id, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없음"));

    if (request.newName() != null && !request.newName().isEmpty()) {
      channel.updateName(request.newName());
    }
    if (request.newDescription() != null && !request.newDescription().isEmpty()) {
      channel.updateDescription(request.newDescription());
    }
  }

  //채널 삭제
  @Override
  @Transactional
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없음"));
    channelRepository.delete(channel);
  }


  /* 새로 만드는 객체는 영속성 컨텍스트에 등록되지 않음
   * 영속 상태로 올려주는게 save()
   */

  //비공개채널 생성
  @Override
  @Transactional
  public void createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel channel = request.toEntity();
    channelRepository.save(channel);
  }

  //공개채널 생성
  @Override
  @Transactional
  public void createPublicChannel(PublicChannelCreateRequest request) {
    Channel channel = request.toEntity();
    channelRepository.save(channel);
  }
}
