package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.channel.UsersNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createPrivateChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

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

    @InjectMocks
    private ChannelService channelService;

    @Test
    @DisplayName("create(PublicChannelCreateRequest) - 공개 채널 생성 성공")
    void createPublicChannel_Success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("  General  ", "  General discussion  ");
        Channel savedChannel = createPublicChannel("General", "General discussion");
        ChannelDto expectedDto = createPublicChannelDto("General discussion");

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("General");

        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("create(PublicChannelCreateRequest) - description이 null인 경우 null로 저장")
    void createPublicChannel_WithNullDescription_Success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", null);
        Channel savedChannel = createPublicChannel("General", null);
        ChannelDto expectedDto = createPublicChannelDto(null);

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("General");
        assertThat(result.description()).isNull();

        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("create(PrivateChannelCreateRequest) - 비공개 채널 생성 성공")
    void createPrivateChannel_Success() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(userId1, userId2, userId3);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        List<User> participants = List.of(
            createUser("user1"), createUser("user2"), createUser("user3")
        );

        Channel savedChannel = createPrivateChannel();
        setField(savedChannel, "createdAt", Instant.now());
        ChannelDto expectedDto = createPrivateChannelDto();

        given(userRepository.findAllByIdIn(participantIds)).willReturn(participants);
        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);

        then(userRepository).should().findAllByIdIn(participantIds);
        then(channelRepository).should().save(any(Channel.class));
        then(readStatusRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("create(PrivateChannelCreateRequest) - 존재하지 않는 사용자 포함 시 UsersNotFoundException 발생")
    void createPrivateChannel_UserNotFound_ThrowsUsersNotFoundException() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(userId1, userId2);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        List<User> foundUsers = List.of(createUser("user1"));

        given(userRepository.findAllByIdIn(participantIds)).willReturn(foundUsers);

        // when & then
        assertThatThrownBy(() -> channelService.create(request))
            .isInstanceOf(UsersNotFoundException.class);

        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("create(PrivateChannelCreateRequest) - 2인 채널이 이미 존재하면 DuplicateChannelException 발생")
    void createPrivateChannel_DuplicateTwoPersonChannel_ThrowsDuplicateChannelException() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Set<UUID> participantIds = Set.of(userId1, userId2);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        List<User> participants = List.of(
            createUserWithId(userId1, "user1"),
            createUserWithId(userId2, "user2")
        );

        given(userRepository.findAllByIdIn(participantIds)).willReturn(participants);
        given(channelRepository.existsBetweenUsers(userId1, userId2)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> channelService.create(request))
            .isInstanceOf(DuplicateChannelException.class);

        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("findAll - 채널이 없을 때 빈 리스트 반환")
    void findAll_EmptyChannels_ReturnsEmptyList() {
        // given
        UUID userId = UUID.randomUUID();

        given(channelRepository.findAllByUserId(userId)).willReturn(List.of());

        // when
        List<ChannelDto> result = channelService.findAll(userId);

        // then
        assertThat(result).isEmpty();

        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 공개 채널 수정 성공")
    void update_PublicChannel_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("  New Name  ", "  New Description  ");
        Channel channel = createPublicChannelWithId("Old Name", "Old Description", channelId);
        ChannelDto expectedDto = createPublicChannelDto(channelId, "New Name", "New Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
        then(messageRepository).should().findLastMessageAtByChannelId(channelId);
    }

    @Test
    @DisplayName("update - newName이 null일 때 이름 업데이트 안함")
    void update_WithNullNewName_DoesNotUpdateName() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(null, "New Description");
        Channel channel = createPublicChannelWithId("Old Name", "Old Description", channelId);
        ChannelDto expectedDto = createPublicChannelDto(channelId, "Old Name", "New Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
    }

    @Test
    @DisplayName("update - newDescription이 null일 때 설명 업데이트 안함")
    void update_WithNullNewDescription_DoesNotUpdateDescription() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name", null);
        Channel channel = createPublicChannelWithId("Old Name", "Old Description", channelId);
        ChannelDto expectedDto = createPublicChannelDto(channelId, "New Name", "Old Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any(Channel.class), anyList(), any())).willReturn(expectedDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
    }

    @Test
    @DisplayName("update - 비공개 채널 수정 시도 시 PrivateChannelUpdateException 발생")
    void update_PrivateChannel_ThrowsPrivateChannelUpdateException() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name", null);
        Channel privateChannel = createPrivateChannel();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, request))
            .isInstanceOf(PrivateChannelUpdateException.class);

        then(channelMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("update - 존재하지 않는 채널 수정 시 ChannelNotFoundException 발생")
    void update_ChannelNotFound_ThrowsChannelNotFoundException() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("New Name", null);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, request))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("delete - 채널 삭제 성공")
    void delete_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = createPublicChannelWithId("Test Channel", null, channelId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        channelService.delete(channelId);

        // then
        then(messageRepository).should().deleteAllByChannelId(channelId);
        then(readStatusRepository).should().deleteAllByChannelId(channelId);
        then(channelRepository).should().delete(channel);
    }

    @Test
    @DisplayName("delete - 존재하지 않는 채널 삭제 시 ChannelNotFoundException 발생")
    void delete_ChannelNotFound_ThrowsChannelNotFoundException() {
        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.delete(channelId))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should(never()).delete(any(Channel.class));
    }

    // ========== Helper Methods ==========

    private Channel createPublicChannelWithId(String name, String description, UUID channelId) {
        Channel channel = createPublicChannel(name, description);
        setField(channel, "id", channelId);
        return channel;
    }

    private ChannelDto createPublicChannelDto(String description) {
        return createPublicChannelDto(UUID.randomUUID(), "General", description);
    }

    private ChannelDto createPublicChannelDto(UUID channelId, String name, String description) {
        return new ChannelDto(channelId, ChannelType.PUBLIC, name, description, List.of(), null);
    }

    private ChannelDto createPrivateChannelDto() {
        return new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, List.of(), null);
    }
}
