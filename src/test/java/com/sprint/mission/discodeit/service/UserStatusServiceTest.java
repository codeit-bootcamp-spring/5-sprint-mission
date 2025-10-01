package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicUserStatusService userStatusService;

    @Test
    @DisplayName("사용자 상태 생성 성공")
    void create_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("testuser").build();
        UserStatus userStatus = new UserStatus(user);

        UserStatusCreateRequest request = UserStatusCreateRequest.builder()
                .userId(userId)
                .build();

        given(userRepository.existsById(userId)).willReturn(true);
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);

        // when
        UserStatusResponse response = userStatusService.create(request);

        // then
        assertThat(response).isNotNull();
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 상태 생성 실패")
    void create_userNotFound_failure() {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusCreateRequest request = UserStatusCreateRequest.builder()
                .userId(userId)
                .build();

        given(userRepository.existsById(userId)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> userStatusService.create(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("이미 존재하는 사용자 상태 생성 실패")
    void create_alreadyExists_failure() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("testuser").build();
        UserStatus existingStatus = new UserStatus(user);

        UserStatusCreateRequest request = UserStatusCreateRequest.builder()
                .userId(userId)
                .build();

        given(userRepository.existsById(userId)).willReturn(true);
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(existingStatus));

        // when
        // then
        assertThatThrownBy(() -> userStatusService.create(request))
                .isInstanceOf(AlreadyExistsUserStatusException.class);
    }

    @Test
    @DisplayName("모든 사용자 상태 조회 성공")
    void getAll_success() {
        // given
        User user1 = User.builder().id(UUID.randomUUID()).username("user1").build();
        User user2 = User.builder().id(UUID.randomUUID()).username("user2").build();

        List<UserStatus> userStatuses = List.of(
                new UserStatus(user1),
                new UserStatus(user2)
        );

        given(userStatusRepository.findAll()).willReturn(userStatuses);

        // when
        List<UserStatusResponse> responses = userStatusService.getAll();

        // then
        assertThat(responses).hasSize(2);
        verify(userStatusRepository).findAll();
    }

    @Test
    @DisplayName("사용자 ID로 상태 업데이트 성공")
    void updateByUserId_success() {
        // given
        UUID userId = UUID.randomUUID();
        Instant newLastActiveAt = Instant.now();

        User user = User.builder().id(userId).username("testuser").build();
        UserStatus userStatus = new UserStatus(user);

        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
                .newLastActiveAt(newLastActiveAt)
                .build();

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userStatusRepository.save(userStatus)).willReturn(userStatus);

        // when
        UserStatusResponse response = userStatusService.updateByUserId(userId, request);

        // then
        assertThat(response).isNotNull();
        verify(userStatusRepository).save(userStatus);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 상태 업데이트 실패")
    void updateByUserId_notFound_failure() {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
                .newLastActiveAt(Instant.now())
                .build();

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
                .isInstanceOf(UserStatusNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 상태 삭제 성공")
    void delete_success() {
        // given
        UUID userStatusId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).username("testuser").build();
        UserStatus userStatus = new UserStatus(user);

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));

        // when
        UserStatusResponse response = userStatusService.delete(userStatusId);

        // then
        assertThat(response).isNotNull();
        verify(userStatusRepository).deleteById(userStatusId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 상태 삭제 실패")
    void delete_notFound_failure() {
        // given
        UUID userStatusId = UUID.randomUUID();
        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userStatusService.delete(userStatusId))
                .isInstanceOf(UserStatusNotFoundException.class);
    }
}