package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusForbiddenException;
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

import static com.sprint.mission.discodeit.support.TestFixtures.TEST_CHANNEL_NAME;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_USERNAME;
import static com.sprint.mission.discodeit.support.TestFixtures.createPrivateChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
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
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            channelId,
            lastReadAt
        );

        User user = createUser(TEST_USERNAME);
        Channel channel = createPublicChannel(TEST_CHANNEL_NAME);  // PUBLIC → notificationEnabled=false
        ReadStatus savedReadStatus = new ReadStatus(user, channel, lastReadAt, false);

        ReadStatusDto expectedDto = new ReadStatusDto(
            UUID.randomUUID(),
            requesterId,
            channelId,
            lastReadAt,
            false
        );

        given(userRepository.findById(requesterId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(savedReadStatus);
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.create(requesterId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(requesterId);
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.lastReadAt()).isEqualTo(lastReadAt);

        then(userRepository).should().findById(requesterId);
        then(channelRepository).should().findById(channelId);
        then(readStatusMapper).should().toDto(any(ReadStatus.class));
    }

    @Test
    @DisplayName("create - PRIVATE 채널의 경우 notificationEnabled가 true로 설정")
    void create_PrivateChannel_NotificationEnabledTrue() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            channelId,
            lastReadAt
        );

        User user = createUser(TEST_USERNAME);
        Channel channel = createPrivateChannel();  // PRIVATE → notificationEnabled=true
        ReadStatus savedReadStatus = new ReadStatus(user, channel, lastReadAt, true);

        ReadStatusDto expectedDto = new ReadStatusDto(
            UUID.randomUUID(),
            requesterId,
            channelId,
            lastReadAt,
            true
        );

        given(userRepository.findById(requesterId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(savedReadStatus);
        given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.create(requesterId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.notificationEnabled()).isTrue();

        then(userRepository).should().findById(requesterId);
        then(channelRepository).should().findById(channelId);
        then(readStatusMapper).should().toDto(any(ReadStatus.class));
    }

    @Test
    @DisplayName("create - 존재하지 않는 사용자로 생성 시 UserNotFoundException 발생")
    void create_UserNotFound_ThrowsUserNotFoundException() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            channelId,
            Instant.now()
        );

        given(userRepository.findById(requesterId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(requesterId, request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(requesterId);
        then(channelRepository).shouldHaveNoInteractions();
        then(readStatusMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("create - 존재하지 않는 채널로 생성 시 ChannelNotFoundException 발생")
    void create_ChannelNotFound_ThrowsChannelNotFoundException() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            channelId,
            Instant.now()
        );

        User user = createUser(TEST_USERNAME);

        given(userRepository.findById(requesterId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.create(requesterId, request))
            .isInstanceOf(ChannelNotFoundException.class);

        then(userRepository).should().findById(requesterId);
        then(channelRepository).should().findById(channelId);
        then(readStatusMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 읽음 상태 수정 성공")
    void update_Success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Instant newLastReadAt = Instant.now();

        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt, false);

        User user = mock(User.class);
        ReadStatus readStatus = mock(ReadStatus.class);

        ReadStatusDto expectedDto = new ReadStatusDto(
            readStatusId,
            requesterId,
            UUID.randomUUID(),
            newLastReadAt,
            false
        );

        given(user.getId()).willReturn(requesterId);
        given(readStatus.getUser()).willReturn(user);
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.update(readStatusId, requesterId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.lastReadAt()).isEqualTo(newLastReadAt);

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatus).should().update(newLastReadAt, false);
        then(readStatusMapper).should().toDto(readStatus);
    }

    @Test
    @DisplayName("update - null로 수정 시 업데이트하지 않음")
    void update_WithNull_DoesNotUpdate() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null, false);

        User user = mock(User.class);
        ReadStatus readStatus = mock(ReadStatus.class);
        ReadStatusDto expectedDto = new ReadStatusDto(
            readStatusId,
            requesterId,
            UUID.randomUUID(),
            Instant.now(),
            false
        );

        given(user.getId()).willReturn(requesterId);
        given(readStatus.getUser()).willReturn(user);
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

        // when
        ReadStatusDto result = readStatusService.update(readStatusId, requesterId, request);

        // then
        assertThat(result).isNotNull();

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatusMapper).should().toDto(readStatus);
    }

    @Test
    @DisplayName("update - 존재하지 않는 읽음 상태 수정 시 ReadStatusNotFoundException 발생")
    void update_ReadStatusNotFound_ThrowsReadStatusNotFoundException() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now(), false);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> readStatusService.update(readStatusId, requesterId, request))
            .isInstanceOf(ReadStatusNotFoundException.class);

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatusMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 다른 사용자의 읽음 상태 수정 시 ReadStatusForbiddenException 발생")
    void update_Forbidden_ThrowsReadStatusForbiddenException() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now(), false);

        User owner = mock(User.class);
        ReadStatus readStatus = mock(ReadStatus.class);

        given(owner.getId()).willReturn(ownerId);
        given(readStatus.getUser()).willReturn(owner);
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));

        // when & then
        assertThatThrownBy(() -> readStatusService.update(readStatusId, requesterId, request))
            .isInstanceOf(ReadStatusForbiddenException.class);

        then(readStatusRepository).should().findById(readStatusId);
        then(readStatusMapper).shouldHaveNoInteractions();
    }
}
