package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.impl.ChannelServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/* 단위 테스트
 * 서비스 레이어의 주요 메소드에 대한 단위 테스트
 * DB, 외부 API 연결하지 않고 Mock을 사용해 검증
 */

class ChannelServiceImplTest {

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private ChannelServiceImpl channelService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /* 공개채널 create 성공
   */
  @Test
  void createPublicChannel_success() {
    // given
    ChannelDto dto = new ChannelDto();
    dto.setName("공개방");
    dto.setDescription("설명");

    Channel channel = new Channel("공개방", "설명", ChannelType.PUBLIC);
    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(channelRepository.save(any(Channel.class))).willReturn(channel);

    // when & then
    channelService.createPublicChannel(dto);
    // 예외 없으면 성공
  }

  /* 비공개채널 create 성공
   */
  @Test
  void createPrivateChannel_success() {
    // given
    ChannelDto dto = new ChannelDto();
    dto.setName("비공개방");
    dto.setDescription("설명");

    Channel channel = new Channel("비공개방", "설명", ChannelType.PRIVATE);
    given(channelMapper.toEntity(dto)).willReturn(channel);
    given(channelRepository.save(any(Channel.class))).willReturn(channel);

    // when & then
    channelService.createPrivateChannel(dto);
    // 예외 없으면 성공
  }

  /* 채널 생성 실패 (mapper가 null 반환)
   */
  @Test
  void createChannel_fail_mapperNull() {
    // given
    ChannelDto dto = new ChannelDto();
    dto.setName("null방");

    given(channelMapper.toEntity(dto)).willReturn(null);

    // when & then
    assertThatThrownBy(() -> channelService.createPublicChannel(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("채널 변환 실패");
  }

  /* 채널 정보 수정 성공
   */
  @Test
  void updateChannel_success() {
    // given
    UUID channelId = UUID.randomUUID();
    ChannelDto updateDto = new ChannelDto();
    updateDto.setName("수정채널");
    Channel channel = new Channel("기존채널", "설명", ChannelType.PUBLIC);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    // (Dirty Checking이라 save 불필요)

    // when
    channelService.update(channelId, updateDto);
    // then (예외 없으면 성공)
  }

  /* 채널 정보 수정 실패 (없는 채널)
   */
  @Test
  void updateChannel_fail_notFound() {
    UUID channelId = UUID.randomUUID();
    ChannelDto updateDto = new ChannelDto();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(channelId, updateDto))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  /* 채널 삭제 성공
   */
  @Test
  void deleteChannel_success() {
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel("삭제방", "설명", ChannelType.PUBLIC);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    willDoNothing().given(channelRepository).delete(channel);

    channelService.delete(channelId);
    // 예외 없으면 성공
  }

  /* 채널 삭제 실패 (없는 채널)
   */
  @Test
  void deleteChannel_fail_notFound() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  /* 단건 조회 성공
   */
  @Test
  void findById_success() {
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel("조회방", "설명", ChannelType.PUBLIC);
    ChannelDto dto = new ChannelDto();
    dto.setName("조회방");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toDto(channel)).willReturn(dto);

    ChannelDto result = channelService.findById(channelId);

    assertThat(result.getName()).isEqualTo("조회방");
  }

  /* 단건 조회 실패
   */
  @Test
  void findById_fail_notFound() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.findById(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  /* 전체조회 성공
   */
  @Test
  void findAll_success() {
    Channel channel1 = new Channel("A", "desc", ChannelType.PUBLIC);
    Channel channel2 = new Channel("B", "desc", ChannelType.PRIVATE);
    List<Channel> channelList = Arrays.asList(channel1, channel2);

    ChannelDto dto1 = new ChannelDto();
    dto1.setName("A");
    ChannelDto dto2 = new ChannelDto();
    dto2.setName("B");

    given(channelRepository.findAll()).willReturn(channelList);
    given(channelMapper.toDtoList(channelList)).willReturn(Arrays.asList(dto1, dto2));

    List<ChannelDto> result = channelService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("A");
    assertThat(result.get(1).getName()).isEqualTo("B");
  }
}
