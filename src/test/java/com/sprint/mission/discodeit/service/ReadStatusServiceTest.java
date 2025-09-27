package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReadStatusServiceTest {

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private BasicReadStatusService readStatusService;

    @Test
    @DisplayName("새로운 읽음 상태 생성 성공")
    void create_newReadStatus_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();

        User user = User.builder().id(userId).username("test").build();
        Channel channel = Channel.builder().id(channelId).name("testchannel").build();
        ReadStatus readStatus = new ReadStatus(user, channel);

        ReadStatusCreateRequest request = ReadStatusCreateRequest.builder()
                .userId(userId)
                .channelId(channelId)
                .lastReadAt(lastReadAt)
                .build();

        given(userRepository.existsById(userId)).willReturn(true);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).willReturn(Optional.empty());
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);

        // when
        ReadStatusResponse response = readStatusService.create(request);

        // then
        assertThat(response).isNotNull();
        verify(readStatusRepository).save(any(ReadStatus.class));
    }

    @Test
    @DisplayName("기존 읽음 상태 업데이트")
    void create_existingReadStatus_update() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant newLastReadAt = Instant.now();

        User user = User.builder().id(userId).username("test").build();
        Channel channel = Channel.builder().id(channelId).name("testchannel").build();
        ReadStatus existingReadStatus = new ReadStatus(user, channel);

        ReadStatusCreateRequest request = ReadStatusCreateRequest.builder()
                .userId(userId)
                .channelId(channelId)
                .lastReadAt(newLastReadAt)
                .build();

        given(userRepository.existsById(userId)).willReturn(true);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(readStatusRepository.findByUserIdAndChannelId(userId, channelId))
                .willReturn(Optional.of(existingReadStatus));
        given(readStatusRepository.findByChannelIdAndUserId(channelId, userId))
                .willReturn(existingReadStatus);
        given(readStatusRepository.save(existingReadStatus)).willReturn(existingReadStatus);

        // when
        ReadStatusResponse response = readStatusService.create(request);

        // then
        assertThat(response).isNotNull();
        verify(readStatusRepository).save(existingReadStatus);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 읽음 상태 생성 실패")
    void create_userNotFound_failure() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ReadStatusCreateRequest request = ReadStatusCreateRequest.builder()
                .userId(userId)
                .channelId(channelId)
                .lastReadAt(Instant.now())
                .build();

        given(userRepository.existsById(userId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> readStatusService.create(request))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("ID로 읽음 상태 조회 성공")
    void getById_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).username("test").build();
        Channel channel = Channel.builder().id(UUID.randomUUID()).name("testchannel").build();
        ReadStatus readStatus = new ReadStatus(user, channel);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));

        // when
        ReadStatusResponse response = readStatusService.getById(readStatusId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 실패")
    void getById_notFound_failure() {
        // given
        UUID readStatusId = UUID.randomUUID();
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> readStatusService.getById(readStatusId))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 업데이트 성공")
    void updateById_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        Instant newLastReadAt = Instant.now();

        User user = User.builder().id(UUID.randomUUID()).username("testuser").build();
        Channel channel = Channel.builder().id(UUID.randomUUID()).name("testchannel").build();
        ReadStatus readStatus = new ReadStatus(user, channel);

        ReadStatusUpdateRequest request = ReadStatusUpdateRequest.builder()
                .newLastReadAt(newLastReadAt)
                .build();

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
        given(readStatusRepository.save(readStatus)).willReturn(readStatus);

        // when
        ReadStatusResponse response = readStatusService.updateById(readStatusId, request);

        // then
        assertThat(response).isNotNull();
        verify(readStatusRepository).save(readStatus);
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 업데이트 실패")
    void updateById_notFound_failure() {
        // given
        UUID readStatusId = UUID.randomUUID();
        ReadStatusUpdateRequest request = ReadStatusUpdateRequest.builder()
                .newLastReadAt(Instant.now())
                .build();

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> readStatusService.updateById(readStatusId, request))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }

    @Test
    @DisplayName("읽음 상태 삭제 성공")
    void delete_success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).username("testuser").build();
        Channel channel = Channel.builder().id(UUID.randomUUID()).name("testchannel").build();
        ReadStatus readStatus = new ReadStatus(user, channel);

        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));

        // when
        ReadStatusResponse response = readStatusService.delete(readStatusId);

        // then
        assertThat(response).isNotNull();
        verify(readStatusRepository).deleteById(readStatusId);
    }

    @Test
    @DisplayName("존재하지 않는 읽음 상태 삭제 실패")
    void delete_notFound_failure() {
        // given
        UUID readStatusId = UUID.randomUUID();
        given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> readStatusService.delete(readStatusId))
                .isInstanceOf(ReadStatusNotFoundException.class);
    }
}