package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.domain.channel.ChannelRepository;
import com.sprint.mission.discodeit.domain.channel.ChannelService;
import com.sprint.mission.discodeit.domain.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.domain.channel.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.domain.channel.exception.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.domain.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private ChannelService channelService;

    @Test
    @DisplayName("채널 생성 성공 테스트")
    void createChannel_success() {
        // given
        final PublicChannelCreateRequest request = new PublicChannelCreateRequest("새로운 채널", "새로운 채널 설명 입니다."); // DTO 예시
        final Channel savedChannel = new Channel(ChannelType.PUBLIC, request.name(), request.description()); // Entity 예시
        final ChannelDto channelDto = ChannelDto.builder().name("새로운 채널").build(); // DTO 예시

        BDDMockito.given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        BDDMockito.given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto resultDto = channelService.create(request);

        // then
        assertThat(resultDto.name()).isEqualTo("새로운 채널");
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("채널 생성 실패 테스트 - 이미 존재하는 채널")
    void createChannel_failure_alreadyExists() {
        // given
        final PublicChannelCreateRequest request = new PublicChannelCreateRequest("이미 있는 채널", "이미 존재하는 채널 설명");
        BDDMockito.given(channelRepository.existsByName(anyString())).willReturn(true);


        // when & then
        assertThrows(ChannelAlreadyExistsException.class, () -> {
            channelService.create(request);
        });

        // save 메서드가 호출되지 않았는지 검증
        then(channelRepository).should(never()).save(any(Channel.class));
    }
}