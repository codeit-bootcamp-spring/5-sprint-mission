package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.InvalidParticipantException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_publicChannel_success() {
        // given
        PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
                .name("test")
                .description("test")
                .build();

        given(channelRepository.existsByName("test")).willReturn(false);

        Channel savedChannel = Channel.builder()
                .id(UUID.randomUUID())
                .name("test")
                .description("test")
                .type(ChannelType.PUBLIC)
                .build();

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

        User user1 = User.builder().id(UUID.randomUUID()).username("user1").build();
        User user2 = User.builder().id(UUID.randomUUID()).username("user2").build();
        List<User> allUsers = Arrays.asList(user1, user2);

        given(userRepository.findAll()).willReturn(allUsers);
        given(messageRepository.findLatestMessageTimeByChannelId(any(UUID.class))).willReturn(Optional.empty());

        // when
        ChannelResponse result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.getDescription()).isEqualTo("test");

        then(channelRepository).should().existsByName("test");
        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAll();
        then(readStatusRepository).should(times(2)).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("중복된 채널명으로 공개 채널 생성 실패")
    void create_publicChannel_fail_duplicateName() {
        // given
        PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
                .name("existingChannel")
                .description("Description")
                .build();

        given(channelRepository.existsByName("existingChannel")).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> channelService.create(request))
                .isInstanceOf(DuplicateChannelNameException.class);

        then(channelRepository).should().existsByName("existingChannel");
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("private 채널 생성 성공")
    void create_privateChannel_success() {
        // given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        List<UUID> participantIds = Arrays.asList(user1Id, user2Id);

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(participantIds)
                .build();

        given(userRepository.existsById(user1Id)).willReturn(true);
        given(userRepository.existsById(user2Id)).willReturn(true);

        Channel savedChannel = Channel.builder()
                .id(UUID.randomUUID())
                .type(ChannelType.PRIVATE)
                .name("private-" + UUID.randomUUID())
                .build();

        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

        User user1 = User.builder().id(user1Id).username("user1").build();
        User user2 = User.builder().id(user2Id).username("user2").build();

        given(userRepository.findById(user1Id)).willReturn(Optional.of(user1));
        given(userRepository.findById(user2Id)).willReturn(Optional.of(user2));
        given(readStatusRepository.findUsersByChannelId(any(UUID.class))).willReturn(Arrays.asList(user1, user2));
        given(messageRepository.findLatestMessageTimeByChannelId(any(UUID.class))).willReturn(Optional.empty());

        // when
        ChannelResponse result = channelService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.getParticipants()).hasSize(2);

        then(userRepository).should().existsById(user1Id);
        then(userRepository).should().existsById(user2Id);
        then(channelRepository).should().save(any(Channel.class));
        then(readStatusRepository).should(times(2)).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("존재하지 않는 참여자 포함으로 private 채널 생성 실패")
    void create_privateChannel_fail_userNotExists() {
        // given
        UUID existingUserId = UUID.randomUUID();
        UUID nonExistingUserId = UUID.randomUUID();
        List<UUID> participantIds = Arrays.asList(existingUserId, nonExistingUserId);

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(participantIds)
                .build();

        given(userRepository.existsById(existingUserId)).willReturn(true);
        given(userRepository.existsById(nonExistingUserId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> channelService.create(request))
                .isInstanceOf(InvalidParticipantException.class);

        then(userRepository).should().existsById(existingUserId);
        then(userRepository).should().existsById(nonExistingUserId);
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("참여자 수 부족으로 private 채널 생성 실패")
    void create_privateChannel_fail_insufficientParticipants() {
        // given
        UUID singleUserId = UUID.randomUUID();
        List<UUID> participantIds = List.of(singleUserId);

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(participantIds)
                .build();

        // when
        // then
        assertThatThrownBy(() -> channelService.create(request))
                .isInstanceOf(InvalidParticipantException.class);

        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("채널 정보 수정 성공")
    void update_channel_Success() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest request = ChannelUpdateRequest.builder()
                .newName("updatedChannel")
                .newDescription("Updated description")
                .build();

        Channel existingChannel = Channel.builder()
                .id(channelId)
                .name("channel")
                .description("description")
                .type(ChannelType.PUBLIC)
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));
        given(channelRepository.existsByName("updatedChannel")).willReturn(false);
        given(channelRepository.save(any(Channel.class))).willReturn(existingChannel);
        given(messageRepository.findLatestMessageTimeByChannelId(any(UUID.class))).willReturn(Optional.empty());

        // when
        ChannelResponse result = channelService.updateChannel(channelId, request);

        // then
        assertThat(result).isNotNull();

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should().existsByName("updatedChannel");
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("private 채널 정보 수정 실패")
    void update_channel_fail_privateChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest request = ChannelUpdateRequest.builder()
                .newName("updatedName")
                .build();

        Channel privateChannel = Channel.builder()
                .id(channelId)
                .name("privateChannel")
                .type(ChannelType.PRIVATE)
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when
        // then
        assertThatThrownBy(() -> channelService.updateChannel(channelId, request))
                .isInstanceOf(PrivateChannelUpdateException.class);

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("채널 정보 수정 실패 - 존재하지 않는 채널")
    void update_channel_fail_channelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequest request = ChannelUpdateRequest.builder()
                .newName("updatedChannel")
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> channelService.updateChannel(channelId, request))
                .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should(never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_channel_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel existingChannel = Channel.builder()
                .id(channelId)
                .name("channel")
                .type(ChannelType.PUBLIC)
                .build();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(existingChannel));

        // when
        ChannelDeleteResponse result = channelService.deleteChannel(channelId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChannelId()).isEqualTo(channelId);
        assertThat(result.getName()).isEqualTo("channel");

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should().deleteById(channelId);
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 실패")
    void delete_channel_fail_channelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> channelService.deleteChannel(channelId))
                .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(channelRepository).should(never()).deleteById(channelId);
    }

    @Test
    @DisplayName("사용자별 채널 조회 성공")
    void findByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channel1Id = UUID.randomUUID();
        UUID channel2Id = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("test")
                .build();

        Channel channel1 = Channel.builder()
                .id(channel1Id)
                .name("public")
                .type(ChannelType.PUBLIC)
                .build();

        Channel channel2 = Channel.builder()
                .id(channel2Id)
                .name("private")
                .type(ChannelType.PRIVATE)
                .build();

        ReadStatus readStatus1 = ReadStatus.builder()
                .id(UUID.randomUUID())
                .user(user)
                .channel(channel1)
                .build();

        ReadStatus readStatus2 = ReadStatus.builder()
                .id(UUID.randomUUID())
                .user(user)
                .channel(channel2)
                .build();

        given(readStatusRepository.findByUserId(userId))
                .willReturn(Arrays.asList(readStatus1, readStatus2));
        given(channelRepository.findAll())
                .willReturn(Arrays.asList(channel1, channel2));
        given(messageRepository.findLatestMessageTimeByChannelId(channel1Id))
                .willReturn(Optional.empty());
        given(messageRepository.findLatestMessageTimeByChannelId(channel2Id))
                .willReturn(Optional.empty());
        given(readStatusRepository.findUsersByChannelId(channel2Id))
                .willReturn(List.of(user));

        // when
        List<ChannelResponse> result = channelService.findChannelsByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ChannelResponse::getName)
                .containsExactlyInAnyOrder("public", "private");

        then(readStatusRepository).should().findByUserId(userId);
        then(channelRepository).should().findAll();
        then(messageRepository).should(times(2)).findLatestMessageTimeByChannelId(any(UUID.class));
    }

    @Test
    @DisplayName("사용자별 채널 조회")
    void findByUserId_empty() {
        // given
        UUID userId = UUID.randomUUID();
        given(readStatusRepository.findByUserId(userId)).willReturn(List.of());
        given(channelRepository.findAll()).willReturn(List.of());

        // when
        List<ChannelResponse> result = channelService.findChannelsByUserId(userId);

        // then
        assertThat(result).isEmpty();

        then(readStatusRepository).should().findByUserId(userId);
        then(channelRepository).should().findAll();
    }
}
