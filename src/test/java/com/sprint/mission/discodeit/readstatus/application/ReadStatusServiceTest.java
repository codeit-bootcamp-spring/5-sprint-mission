package com.sprint.mission.discodeit.readstatus.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadStatusService 단위 테스트")
class ReadStatusServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusMapper readStatusMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ReadStatusService readStatusService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CHANNEL_ID = UUID.randomUUID();
    private static final UUID READ_STATUS_ID = UUID.randomUUID();
    private static final Instant LAST_READ_AT = Instant.now();

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("PUBLIC 채널 ReadStatus 생성 시 성공")
        void create_withPublicChannel_success() {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(CHANNEL_ID, LAST_READ_AT);

            User user = mock(User.class);
            Channel channel = mock(Channel.class);
            ReadStatus savedReadStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = createReadStatusDto(false);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(readStatusRepository.findByUserIdAndChannelId(USER_ID, CHANNEL_ID))
                .willReturn(Optional.empty());
            given(channel.getType()).willReturn(ChannelType.PUBLIC);
            given(readStatusRepository.save(any(ReadStatus.class))).willReturn(savedReadStatus);
            given(readStatusMapper.toDto(savedReadStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.create(USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatusRepository).should().save(any(ReadStatus.class));
            then(cacheService).should(never()).evict(any(), any());
        }

        @Test
        @DisplayName("PRIVATE 채널 ReadStatus 생성 시 성공 및 캐시 무효화")
        void create_withPrivateChannel_success() {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(CHANNEL_ID, LAST_READ_AT);

            User user = mock(User.class);
            Channel channel = mock(Channel.class);
            ReadStatus savedReadStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = createReadStatusDto(true);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(readStatusRepository.findByUserIdAndChannelId(USER_ID, CHANNEL_ID))
                .willReturn(Optional.empty());
            given(channel.getType()).willReturn(ChannelType.PRIVATE);
            given(readStatusRepository.save(any(ReadStatus.class))).willReturn(savedReadStatus);
            given(readStatusMapper.toDto(savedReadStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.create(USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatusRepository).should().save(any(ReadStatus.class));
            then(cacheService).should().evict(CacheName.SUBSCRIBED_CHANNELS, USER_ID);
        }

        @Test
        @DisplayName("이미 존재하는 ReadStatus 시 기존 값 반환")
        void create_withExistingReadStatus_returnsExisting() {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(CHANNEL_ID, LAST_READ_AT);

            User user = mock(User.class);
            Channel channel = mock(Channel.class);
            ReadStatus existingReadStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = createReadStatusDto(true);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.of(channel));
            given(readStatusRepository.findByUserIdAndChannelId(USER_ID, CHANNEL_ID))
                .willReturn(Optional.of(existingReadStatus));
            given(readStatusMapper.toDto(existingReadStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.create(USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatusRepository).should(never()).save(any());
            then(cacheService).should(never()).evict(any(), any());
        }

        @Test
        @DisplayName("사용자 없음 시 UserNotFoundException 발생")
        void create_withNonExistentUser_throwsException() {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(CHANNEL_ID, LAST_READ_AT);

            given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readStatusService.create(USER_ID, request))
                .isInstanceOf(UserNotFoundException.class);

            then(readStatusRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("채널 없음 시 ChannelNotFoundException 발생")
        void create_withNonExistentChannel_throwsException() {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(CHANNEL_ID, LAST_READ_AT);

            User user = mock(User.class);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
            given(channelRepository.findById(CHANNEL_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readStatusService.create(USER_ID, request))
                .isInstanceOf(ChannelNotFoundException.class);

            then(readStatusRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAllByUserId")
    class FindAllByUserIdTest {

        @Test
        @DisplayName("유효한 사용자 ID 조회 시 ReadStatus 목록 반환")
        void findAllByUserId_success() {
            // given
            ReadStatus readStatus1 = mock(ReadStatus.class);
            ReadStatus readStatus2 = mock(ReadStatus.class);
            ReadStatusDto dto1 = createReadStatusDto(true);
            ReadStatusDto dto2 = createReadStatusDto(false);

            given(readStatusRepository.findAllByUserId(USER_ID))
                .willReturn(List.of(readStatus1, readStatus2));
            given(readStatusMapper.toDto(readStatus1)).willReturn(dto1);
            given(readStatusMapper.toDto(readStatus2)).willReturn(dto2);

            // when
            List<ReadStatusDto> result = readStatusService.findAllByUserId(USER_ID);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(dto1, dto2);
        }

        @Test
        @DisplayName("ReadStatus 없음 시 빈 리스트 반환")
        void findAllByUserId_whenEmpty_returnsEmptyList() {
            // given
            given(readStatusRepository.findAllByUserId(USER_ID)).willReturn(List.of());

            // when
            List<ReadStatusDto> result = readStatusService.findAllByUserId(USER_ID);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청 시 ReadStatus 수정 성공")
        void update_success() {
            // given
            Instant newLastReadAt = LAST_READ_AT.plusSeconds(3600);
            Boolean newNotificationEnabled = false;
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt, newNotificationEnabled);

            User user = mock(User.class);
            ReadStatus readStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = new ReadStatusDto(
                READ_STATUS_ID, USER_ID, CHANNEL_ID, newLastReadAt, newNotificationEnabled);

            given(readStatusRepository.findById(READ_STATUS_ID)).willReturn(Optional.of(readStatus));
            given(readStatus.getUser()).willReturn(user);
            given(user.getId()).willReturn(USER_ID);
            given(readStatus.update(newLastReadAt, newNotificationEnabled)).willReturn(readStatus);
            given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.update(READ_STATUS_ID, USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatus).should().update(newLastReadAt, newNotificationEnabled);
        }

        @Test
        @DisplayName("lastReadAt만 입력 시 수정 성공")
        void update_withOnlyLastReadAt_success() {
            // given
            Instant newLastReadAt = LAST_READ_AT.plusSeconds(3600);
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt, null);

            User user = mock(User.class);
            ReadStatus readStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = new ReadStatusDto(
                READ_STATUS_ID, USER_ID, CHANNEL_ID, newLastReadAt, true);

            given(readStatusRepository.findById(READ_STATUS_ID)).willReturn(Optional.of(readStatus));
            given(readStatus.getUser()).willReturn(user);
            given(user.getId()).willReturn(USER_ID);
            given(readStatus.update(newLastReadAt, null)).willReturn(readStatus);
            given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.update(READ_STATUS_ID, USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatus).should().update(newLastReadAt, null);
        }

        @Test
        @DisplayName("notificationEnabled만 입력 시 수정 성공")
        void update_withOnlyNotificationEnabled_success() {
            // given
            Boolean newNotificationEnabled = false;
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null, newNotificationEnabled);

            User user = mock(User.class);
            ReadStatus readStatus = mock(ReadStatus.class);
            ReadStatusDto expectedDto = new ReadStatusDto(
                READ_STATUS_ID, USER_ID, CHANNEL_ID, LAST_READ_AT, newNotificationEnabled);

            given(readStatusRepository.findById(READ_STATUS_ID)).willReturn(Optional.of(readStatus));
            given(readStatus.getUser()).willReturn(user);
            given(user.getId()).willReturn(USER_ID);
            given(readStatus.update(null, newNotificationEnabled)).willReturn(readStatus);
            given(readStatusMapper.toDto(readStatus)).willReturn(expectedDto);

            // when
            ReadStatusDto result = readStatusService.update(READ_STATUS_ID, USER_ID, request);

            // then
            assertThat(result).isEqualTo(expectedDto);
            then(readStatus).should().update(null, newNotificationEnabled);
        }

        @Test
        @DisplayName("ReadStatus 없음 시 ReadStatusNotFoundException 발생")
        void update_withNonExistentReadStatus_throwsException() {
            // given
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(LAST_READ_AT, true);

            given(readStatusRepository.findById(READ_STATUS_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readStatusService.update(READ_STATUS_ID, USER_ID, request))
                .isInstanceOf(ReadStatusNotFoundException.class);
        }

        @Test
        @DisplayName("다른 사용자 ReadStatus 수정 시 ReadStatusForbiddenException 발생")
        void update_withDifferentUser_throwsException() {
            // given
            UUID otherUserId = UUID.randomUUID();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(LAST_READ_AT, true);

            User user = mock(User.class);
            ReadStatus readStatus = mock(ReadStatus.class);

            given(readStatusRepository.findById(READ_STATUS_ID)).willReturn(Optional.of(readStatus));
            given(readStatus.getUser()).willReturn(user);
            given(user.getId()).willReturn(USER_ID);

            // when & then
            assertThatThrownBy(() -> readStatusService.update(READ_STATUS_ID, otherUserId, request))
                .isInstanceOf(ReadStatusForbiddenException.class);

            then(readStatus).should(never()).update(any(), any());
        }
    }

    private ReadStatusDto createReadStatusDto(boolean notificationEnabled) {
        return new ReadStatusDto(
            READ_STATUS_ID,
            USER_ID,
            CHANNEL_ID,
            LAST_READ_AT,
            notificationEnabled
        );
    }
}
