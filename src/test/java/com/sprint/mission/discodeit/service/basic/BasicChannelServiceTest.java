package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService channelService;

    private UUID channelId;
    private UUID userId;
    private String name;
    private String description;
    private Channel channel;
    private ChannelDto channelDto;
    private User user;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        name = "test";
        description = "testDescription";

        channel = new Channel(
            ChannelType.PUBLIC,
            name,
            description
        );
        ReflectionTestUtils.setField(channel, "id", channelId);
        channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, name, description, List.of(),
            Instant.now());
        user = new User("test", "test@email.com", "password1234", null);
    }

    /*CREATE*/

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_public_success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isEqualTo(channelDto);
        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void create_private_success() {
        List<UUID> participantIds = List.of(userId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // given
        given(userRepository.findAllById(eq(participantIds))).willReturn(List.of(user));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isEqualTo(channelDto);
        verify(channelRepository).save(any(Channel.class));
        verify(readStatusRepository).saveAll(anyList());
    }

    /*FINDALL*/

    @Test
    @DisplayName("사용자 아이디로 채널 목록 읽기 성공")
    void findAllByUserId_success() {
        List<ReadStatus> readStatusList = List.of(new ReadStatus(user, channel, Instant.now()));

        // given
        given(readStatusRepository.findAllByUserId(eq(userId))).willReturn(readStatusList);
        given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(channelId)))
            .willReturn(List.of(channel));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        List<ChannelDto> resultList = channelService.findAllByUserId(userId);

        // then
//        assertThat(resultList).isEqualTo(List.of(channelDto));
        assertThat(resultList).contains(channelDto);
    }

    /*UPDATE*/

    @Test
    @DisplayName("채널 수정 성공")
    void update_success() {
        String newName = "newTest";
        String newDescription = "newTestDescription";
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName,
            newDescription);

        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result).isEqualTo(channelDto);
    }

    @Test
    @DisplayName("비공개 채널 수정 성공 실패")
    void update_private_PrivateChannelUpdateException() {
        Channel privateChannel = new Channel(ChannelType.PRIVATE, channel.getName(),
            channel.getDescription());

        String newName = "newTest";
        String newDescription = "newTestDescription";
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName,
            newDescription);

        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when then
        assertThatThrownBy(() -> channelService.update(channelId, request))
            .isInstanceOf(PrivateChannelUpdateException.class);
    }

    /*DELETE*/

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() {
        // given
        given(channelRepository.existsById(eq(channelId))).willReturn(true);

        // when
        channelService.delete(channelId);

        // then
        verify(messageRepository).deleteAllByChannelId(channelId);
        verify(readStatusRepository).deleteAllByChannelId(channelId);
        verify(channelRepository).deleteById(eq(channelId));
    }

    @Test
    @DisplayName("채널 삭제 실패")
    void delete_existsById_ChannelNotFoundException() {
        // given
        given(channelRepository.existsById(eq(channelId))).willReturn(false);

        // when then
        assertThatThrownBy(() -> channelService.delete(channelId))
            .isInstanceOf(ChannelNotFoundException.class);
    }
}