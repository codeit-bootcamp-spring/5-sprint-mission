package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReadStatusServiceTest {

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusMapper readStatusMapper;

    @InjectMocks
    private ReadStatusService readStatusService;

    @Test
    @DisplayName("create - 읽음 상태 생성 성공")
    void create_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            userId,
            channelId,
            lastReadAt
        );

        User user = new User("testuser", "test@example.com", "encoded", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);

        ReadStatusDto expectedDto = new ReadStatusDto(
            UUID.randomUUID(),
            userId,
            channelId,
            lastReadAt
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.lastReadAt()).isEqualTo(lastReadAt);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(readStatusMapper).should().toDto(any(ReadStatus.class));
    }

    @Test
    @DisplayName("create - 존재하지 않는 사용자로 생성 시 UserNotFoundException 발생")
    void create_UserNotFound_ThrowsUserNotFoundException() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            userId,
            channelId,
            Instant.now()
        );

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).shouldHaveNoInteractions();
        then(readStatusMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("create - 존재하지 않는 채널로 생성 시 ChannelNotFoundException 발생")
    void create_ChannelNotFound_ThrowsChannelNotFoundException() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            userId,
            channelId,
            Instant.now()
        );

        User user = new User("testuser", "test@example.com", "encoded", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(request))
            .isInstanceOf(ChannelNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(readStatusMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 읽음 상태 수정 성공")
    void update_Success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        Instant newLastReadAt = Instant.now();

        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);

        User user = new User("testuser", "test@example.com", "encoded", null);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", null);
        ReadStatus readStatus = mock(ReadStatus.class);

        ReadStatusDto expectedDto = new ReadStatusDto(
            readStatusId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            newLastReadAt
        );

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.update(readStatusId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.lastReadAt()).isEqualTo(newLastReadAt);

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatus).should().update(newLastReadAt);
        then(readStatusMapper).should().toDto(readStatus);
    }

    @Test
    @DisplayName("update - null로 수정 시 업데이트하지 않음")
    void update_WithNull_DoesNotUpdate() {
        // given
        UUID readStatusId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null);

        ReadStatus readStatus = mock(ReadStatus.class);
        ReadStatusDto expectedDto = new ReadStatusDto(
            readStatusId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            Instant.now()
        );

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.update(readStatusId, request);

        // then
        assertThat(result).isNotNull();

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatus).shouldHaveNoInteractions();
        then(readStatusMapper).should().toDto(readStatus);
    }

    @Test
    @DisplayName("update - 존재하지 않는 읽음 상태 수정 시 ReadStatusNotFoundException 발생")
    void update_ReadStatusNotFound_ThrowsReadStatusNotFoundException() {
        // given
        UUID readStatusId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.update(readStatusId, request))
            .isInstanceOf(ReadStatusNotFoundException.class);

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatusMapper).shouldHaveNoInteractions();
    }
}
