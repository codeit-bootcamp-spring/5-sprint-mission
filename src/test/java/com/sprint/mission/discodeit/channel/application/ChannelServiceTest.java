package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.DuplicateChannelException;
import com.sprint.mission.discodeit.channel.domain.exception.ParticipantsNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.cache.CacheService;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelService 단위 테스트")
class ChannelServiceTest {

    @Mock
    private ChannelInfoService channelInfoService;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private CacheService cacheService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChannelService channelService;

    @Nested
    @DisplayName("create(PublicChannelCreateRequest)")
    class CreatePublicChannelTest {

        @Test
        @DisplayName("유효한 요청 시 PUBLIC 채널 생성 및 DTO 반환")
        void create_withValidRequest_returnsChannelDto() {
            // given
            PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("general", "General channel");

            Channel savedChannel = new Channel(ChannelType.PUBLIC, "general", "General channel");
            ChannelDto expectedDto = createPublicChannelDto(savedChannel.getId(), "general");

            given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(expectedDto);

            // when
            ChannelDto result = channelService.create(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("general");
            assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);

            then(channelRepository).should().save(any(Channel.class));
        }

        @Test
        @DisplayName("채널 이름 앞뒤 공백 입력 시 제거 후 저장")
        void create_withWhitespaceName_trimsBefore() {
            // given
            PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("  general  ", "  Description  ");

            ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
            Channel savedChannel = new Channel(ChannelType.PUBLIC, "general", "Description");

            given(channelRepository.save(captor.capture())).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(createPublicChannelDto(savedChannel.getId(), "general"));

            // when
            channelService.create(request);

            // then
            Channel captured = captor.getValue();
            assertThat(captured.getName()).isEqualTo("general");
            assertThat(captured.getDescription()).isEqualTo("Description");
        }

        @Test
        @DisplayName("description이 null 시 null로 저장")
        void create_withNullDescription_savesNull() {
            // given
            PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("general", null);

            ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
            Channel savedChannel = new Channel(ChannelType.PUBLIC, "general", null);

            given(channelRepository.save(captor.capture())).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(createPublicChannelDto(savedChannel.getId(), "general"));

            // when
            channelService.create(request);

            // then
            Channel captured = captor.getValue();
            assertThat(captured.getDescription()).isNull();
        }
    }

    @Nested
    @DisplayName("create(PrivateChannelCreateRequest)")
    class CreatePrivateChannelTest {

        @Test
        @DisplayName("유효한 요청 시 PRIVATE 채널 생성 및 캐시 evict")
        void create_withValidRequest_createsChannelAndEvictsCache() {
            // given
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            Set<UUID> participantIds = Set.of(userId1, userId2);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            User user1 = createUser(userId1, "user1");
            User user2 = createUser(userId2, "user2");
            List<User> users = List.of(user1, user2);

            UUID savedChannelId = UUID.randomUUID();
            Channel savedChannel = createMockChannel(savedChannelId);
            ChannelDto expectedDto = createPrivateChannelDto(savedChannelId);

            given(userRepository.findAllWithProfileByIdIn(participantIds)).willReturn(users);
            given(channelRepository.existsBetweenUsers(any(UUID.class), any(UUID.class)))
                .willReturn(false);
            given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(expectedDto);

            // when
            ChannelDto result = channelService.create(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);

            then(readStatusRepository).should().saveAll(anyList());
            then(cacheService).should().evictAll(CacheName.READ_STATUSES, participantIds);
            then(cacheService).should().evictAll(CacheName.SUBSCRIBED_CHANNELS, participantIds);
        }

        @Test
        @DisplayName("참여자가 존재하지 않을 시 ParticipantsNotFoundException 발생")
        void create_withMissingParticipants_throwsException() {
            // given
            UUID existingUserId = UUID.randomUUID();
            UUID missingUserId = UUID.randomUUID();
            Set<UUID> participantIds = Set.of(existingUserId, missingUserId);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            User existingUser = createUser(existingUserId, "existingUser");
            given(userRepository.findAllWithProfileByIdIn(participantIds))
                .willReturn(List.of(existingUser));

            // when & then
            assertThatThrownBy(() -> channelService.create(request))
                .isInstanceOf(ParticipantsNotFoundException.class);

            then(channelRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("2명 참여자 간 중복 채널 존재 시 DuplicateChannelException 발생")
        void create_withDuplicateTwoPersonChannel_throwsException() {
            // given
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            Set<UUID> participantIds = Set.of(userId1, userId2);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            User user1 = createUser(userId1, "user1");
            User user2 = createUser(userId2, "user2");

            given(userRepository.findAllWithProfileByIdIn(participantIds))
                .willReturn(List.of(user1, user2));
            given(channelRepository.existsBetweenUsers(any(UUID.class), any(UUID.class)))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> channelService.create(request))
                .isInstanceOf(DuplicateChannelException.class);

            then(channelRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("3명 이상 참여자 시 중복 채널 체크 생략")
        void create_withMoreThanTwoParticipants_skipsChannelCheck() {
            // given
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            UUID userId3 = UUID.randomUUID();
            Set<UUID> participantIds = Set.of(userId1, userId2, userId3);
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

            User user1 = createUser(userId1, "user1");
            User user2 = createUser(userId2, "user2");
            User user3 = createUser(userId3, "user3");

            UUID savedChannelId = UUID.randomUUID();
            Channel savedChannel = createMockChannel(savedChannelId);

            given(userRepository.findAllWithProfileByIdIn(participantIds))
                .willReturn(List.of(user1, user2, user3));
            given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(createPrivateChannelDto(savedChannelId));

            // when
            channelService.create(request);

            // then
            then(channelRepository).should(never()).existsBetweenUsers(any(), any());
            then(channelRepository).should().save(any(Channel.class));
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTest {

        @Test
        @DisplayName("조회 시 구독 채널과 PUBLIC 채널 병합 후 반환")
        void findAll_withChannels_returnsMergedList() {
            // given
            UUID userId = UUID.randomUUID();
            UUID publicChannelId = UUID.randomUUID();
            UUID privateChannelId = UUID.randomUUID();

            ChannelInfoDto publicChannel = new ChannelInfoDto(
                publicChannelId, ChannelType.PUBLIC, "general", "General");
            ChannelInfoDto privateChannel = new ChannelInfoDto(
                privateChannelId, ChannelType.PRIVATE, null, null);

            // ReadStatus mock - covers line 223
            ReadStatus readStatus = createMockReadStatus(privateChannelId, userId);

            // Message mock - covers lines 231, 233 (duplicate messages for same channel)
            Instant messageTime = Instant.now();
            Message message1 = createMockMessage(publicChannelId, messageTime);
            Message message2 = createMockMessage(publicChannelId, messageTime.minusSeconds(10));

            given(channelInfoService.findSubscribedChannels(userId))
                .willReturn(List.of(privateChannel));
            given(channelInfoService.findPublicChannels())
                .willReturn(List.of(publicChannel));
            given(readStatusRepository.findAllWithUserProfileByChannelIdIn(anyList()))
                .willReturn(List.of(readStatus));
            given(messageRepository.findLastMessageByChannelIdIn(anyList()))
                .willReturn(List.of(message1, message2));
            given(channelMapper.toDtoByInfo(any(ChannelInfoDto.class), anyList(), any()))
                .willAnswer(invocation -> {
                    ChannelInfoDto info = invocation.getArgument(0);
                    return new ChannelDto(
                        info.id(), info.type(), info.name(), info.description(),
                        List.of(), messageTime
                    );
                });

            // when
            List<ChannelDto> result = channelService.findAll(userId);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("채널 없음 시 빈 리스트 반환")
        void findAll_withNoChannels_returnsEmptyList() {
            // given
            UUID userId = UUID.randomUUID();

            given(channelInfoService.findSubscribedChannels(userId)).willReturn(List.of());
            given(channelInfoService.findPublicChannels()).willReturn(List.of());

            // when
            List<ChannelDto> result = channelService.findAll(userId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("중복된 PUBLIC 채널 시 하나만 포함")
        void findAll_withDuplicatePublicChannel_returnsUnique() {
            // given
            UUID userId = UUID.randomUUID();
            UUID publicChannelId = UUID.randomUUID();

            ChannelInfoDto publicChannel = new ChannelInfoDto(
                publicChannelId, ChannelType.PUBLIC, "general", null);

            // 구독 채널에도 같은 PUBLIC 채널 포함
            given(channelInfoService.findSubscribedChannels(userId))
                .willReturn(List.of(publicChannel));
            given(channelInfoService.findPublicChannels())
                .willReturn(List.of(publicChannel));
            given(readStatusRepository.findAllWithUserProfileByChannelIdIn(anyList()))
                .willReturn(List.of());
            given(messageRepository.findLastMessageByChannelIdIn(anyList()))
                .willReturn(List.of());
            given(channelMapper.toDtoByInfo(any(ChannelInfoDto.class), anyList(), any()))
                .willAnswer(invocation -> {
                    ChannelInfoDto info = invocation.getArgument(0);
                    return new ChannelDto(
                        info.id(), info.type(), info.name(), info.description(),
                        List.of(), null
                    );
                });

            // when
            List<ChannelDto> result = channelService.findAll(userId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(publicChannelId);
        }

        @Test
        @DisplayName("조회 시 lastMessageAt 기준 내림차순 정렬")
        void findAll_sortsDescendingByLastMessageAt() {
            // given
            UUID userId = UUID.randomUUID();
            UUID channel1Id = UUID.randomUUID();
            UUID channel2Id = UUID.randomUUID();

            Instant older = Instant.now().minusSeconds(100);
            Instant newer = Instant.now();

            ChannelInfoDto channel1 = new ChannelInfoDto(
                channel1Id, ChannelType.PUBLIC, "older", null);
            ChannelInfoDto channel2 = new ChannelInfoDto(
                channel2Id, ChannelType.PUBLIC, "newer", null);

            given(channelInfoService.findSubscribedChannels(userId)).willReturn(List.of());
            given(channelInfoService.findPublicChannels())
                .willReturn(List.of(channel1, channel2));
            given(readStatusRepository.findAllWithUserProfileByChannelIdIn(anyList()))
                .willReturn(List.of());
            given(messageRepository.findLastMessageByChannelIdIn(anyList()))
                .willReturn(List.of());
            given(channelMapper.toDtoByInfo(any(ChannelInfoDto.class), anyList(), any()))
                .willAnswer(invocation -> {
                    ChannelInfoDto info = invocation.getArgument(0);
                    Instant lastAt = info.id().equals(channel1Id) ? older : newer;
                    return new ChannelDto(
                        info.id(), info.type(), info.name(), info.description(),
                        List.of(), lastAt
                    );
                });

            // when
            List<ChannelDto> result = channelService.findAll(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("newer");
            assertThat(result.get(1).name()).isEqualTo("older");
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("유효한 요청 시 PUBLIC 채널 이름과 설명 수정 성공")
        void update_withValidRequest_updatesChannel() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new-name", "new-description");

            Channel existingChannel = new Channel(ChannelType.PUBLIC, "old-name", "old-description");
            ChannelDto expectedDto = createPublicChannelDto(channelId, "new-name");

            given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
            given(messageRepository.findLastCreatedAtByChannelId(channelId))
                .willReturn(Optional.empty());
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(expectedDto);

            // when
            ChannelDto result = channelService.update(channelId, request);

            // then
            assertThat(result.name()).isEqualTo("new-name");
            assertThat(existingChannel.getName()).isEqualTo("new-name");
            assertThat(existingChannel.getDescription()).isEqualTo("new-description");
        }

        @Test
        @DisplayName("존재하지 않는 채널 수정 시 ChannelNotFoundException 발생")
        void update_withNonExistingChannel_throwsException() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new-name", null);

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(ChannelNotFoundException.class);
        }

        @Test
        @DisplayName("PRIVATE 채널 수정 시 PrivateChannelUpdateException 발생")
        void update_withPrivateChannel_throwsException() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new-name", null);

            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

            // when & then
            assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelUpdateException.class);
        }

        @Test
        @DisplayName("newName이 비어있을 시 기존 이름 유지")
        void update_withEmptyName_keepsOriginalName() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("", "new-description");

            Channel existingChannel = new Channel(ChannelType.PUBLIC, "original", "old-desc");
            ChannelDto expectedDto = createPublicChannelDto(channelId, "original");

            given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
            given(messageRepository.findLastCreatedAtByChannelId(channelId))
                .willReturn(Optional.empty());
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(expectedDto);

            // when
            channelService.update(channelId, request);

            // then
            assertThat(existingChannel.getName()).isEqualTo("original");
        }

        @Test
        @DisplayName("newDescription이 null 시 기존 설명 유지")
        void update_withNullDescription_keepsOriginalDescription() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new-name", null);

            Channel existingChannel = new Channel(ChannelType.PUBLIC, "original", "old-desc");
            ChannelDto expectedDto = createPublicChannelDto(channelId, "new-name");

            given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
            given(messageRepository.findLastCreatedAtByChannelId(channelId))
                .willReturn(Optional.empty());
            given(channelMapper.toDto(any(Channel.class), anyList(), any()))
                .willReturn(expectedDto);

            // when
            channelService.update(channelId, request);

            // then
            assertThat(existingChannel.getDescription()).isEqualTo("old-desc");
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTest {

        @Test
        @DisplayName("존재하는 채널 삭제 시 ChannelDeletedEvent 발행")
        void deleteById_withExistingChannel_deletesAndPublishesEvent() {
            // given
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PUBLIC, "general", null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when
            channelService.deleteById(channelId);

            // then
            then(channelRepository).should().delete(channel);

            ArgumentCaptor<ChannelDeletedEvent> captor =
                ArgumentCaptor.forClass(ChannelDeletedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());

            ChannelDeletedEvent event = captor.getValue();
            assertThat(event.channelId()).isEqualTo(channelId);
            assertThat(event.channelType()).isEqualTo(ChannelType.PUBLIC);
        }

        @Test
        @DisplayName("PRIVATE 채널 삭제 시에도 삭제 성공")
        void deleteById_withPrivateChannel_deletes() {
            // given
            UUID channelId = UUID.randomUUID();
            Channel channel = new Channel(ChannelType.PRIVATE, null, null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            // when
            channelService.deleteById(channelId);

            // then
            then(channelRepository).should().delete(channel);

            ArgumentCaptor<ChannelDeletedEvent> captor =
                ArgumentCaptor.forClass(ChannelDeletedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());

            assertThat(captor.getValue().channelType()).isEqualTo(ChannelType.PRIVATE);
        }

        @Test
        @DisplayName("존재하지 않는 채널 삭제 시 ChannelNotFoundException 발생")
        void deleteById_withNonExistingChannel_throwsException() {
            // given
            UUID channelId = UUID.randomUUID();

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> channelService.deleteById(channelId))
                .isInstanceOf(ChannelNotFoundException.class);

            then(channelRepository).should(never()).delete(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }
    }

    private ChannelDto createPublicChannelDto(UUID id, String name) {
        return new ChannelDto(
            id,
            ChannelType.PUBLIC,
            name,
            null,
            List.of(),
            null
        );
    }

    private ChannelDto createPrivateChannelDto(UUID id) {
        return new ChannelDto(
            id,
            ChannelType.PRIVATE,
            null,
            null,
            List.of(),
            null
        );
    }

    private User createUser(UUID id, String username) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        return user;
    }

    private Channel createMockChannel(UUID id) {
        Channel channel = mock(Channel.class);
        lenient().when(channel.getId()).thenReturn(id);
        lenient().when(channel.getType()).thenReturn(ChannelType.PRIVATE);
        lenient().when(channel.getCreatedAt()).thenReturn(Instant.now());
        return channel;
    }

    private ReadStatus createMockReadStatus(UUID channelId, UUID userId) {
        ReadStatus readStatus = mock(ReadStatus.class);
        Channel channel = mock(Channel.class);
        User user = mock(User.class);

        given(channel.getId()).willReturn(channelId);
        lenient().when(user.getId()).thenReturn(userId);
        given(readStatus.getChannel()).willReturn(channel);
        lenient().when(readStatus.getUser()).thenReturn(user);

        return readStatus;
    }

    private Message createMockMessage(UUID channelId, Instant createdAt) {
        Message message = mock(Message.class);
        Channel channel = mock(Channel.class);

        given(channel.getId()).willReturn(channelId);
        given(message.getChannel()).willReturn(channel);
        lenient().when(message.getCreatedAt()).thenReturn(createdAt);

        return message;
    }

    private UserDto createUserDto(UUID id, String username) {
        return new UserDto(
            id,
            username,
            username + "@test.com",
            null,
            false,
            Role.USER
        );
    }
}
