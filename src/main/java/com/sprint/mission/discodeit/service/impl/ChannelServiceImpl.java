package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

  private final ChannelMapper channelMapper;
  private final ChannelRepository channelRepository;

  //채널 생성
  @Override
  @Transactional
  public void create(Channel channel) {
    channelRepository.save(channel);
    log.info("채널 생성 완료: name={}", channel.getName());
  }

  //채널 단건 조회
  @Override
  @Transactional
  public ChannelDto findById(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException());
    log.info("채널 단건조회 성공: id={}", id);
    return channelMapper.toDto(channel);
  }

  //채널 리스트 조회
  @Override
  @Transactional
  public List<ChannelDto> findAll() {
    log.info("채널 전체 조회");
    List<Channel> channels = channelRepository.findAll();
    return channelMapper.toDtoList(channels);
  }

  /* 채널 정보 수정
   * save 호출X, Dirty Checking
   * 메서드 끝날때 트랜잭션 commit됨
   * */
  @Override
  @Transactional
  public ChannelDto update(UUID id, ChannelDto dto) {
    Channel channel = channelRepository.findById(id) // 영속성 컨텍스트에 의해 관리
        .orElseThrow(() -> new ChannelNotFoundException());

    channelMapper.updateEntityFromDto(channel, dto);
    log.info("[수정 전] name={}, description={}", channel.getName(), channel.getDescription());
    channelMapper.updateEntityFromDto(channel, dto);
    log.info("[수정 후] name={}, description={}", channel.getName(), channel.getDescription());
    return channelMapper.toDto(channel); // ✅ 수정된 엔티티 DTO 변환 후 반환
  }


  //채널 삭제
  @Override
  @Transactional
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException());
    channelRepository.delete(channel);
    log.info("채널 삭제 완료: id={}", id);
  }


  /* 새로 만드는 객체는 영속성 컨텍스트에 등록되지 않음
   * 영속 상태로 올려주는게 save()
   */

  //비공개채널 생성
  @Override
  @Transactional
  public void createPrivateChannel(ChannelDto dto) {
    Channel channel = channelMapper.toEntity(dto);
    channelRepository.save(channel);
    log.info("비공개채널 생성 완료: name={}", dto.getName());
  }

  //공개채널 생성
  @Override
  @Transactional
  public void createPublicChannel(ChannelDto dto) {
    Channel channel = channelMapper.toEntity(dto);
    if (channel == null) {
      throw new IllegalArgumentException("채널 변환 실패(null)");
    }
    channelRepository.save(channel);
    log.info("공개채널 생성 완료: name={}", dto.getName());
  }
}
