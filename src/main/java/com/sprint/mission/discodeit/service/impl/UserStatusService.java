package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateCommand;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateByUserIdCommand;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateCommand;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;

    private static UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(userStatus.getUserId(), userStatus.getStatus());
    }

    private static void apply(UserStatus us,
                              Status status,
                              Boolean login,
                              Boolean logout,
                              Boolean heartbeat,
                              Boolean unfix) {
        if (Boolean.TRUE.equals(logout)) us.logout();
        if (Boolean.TRUE.equals(login)) us.login();
        if (status != null) us.setStatus(status);
        if (Boolean.TRUE.equals(unfix)) us.unfixStatus();
        if (Boolean.TRUE.equals(heartbeat)) us.heartBeat();
    }

    public UserStatusResponse create(UserStatusCreateCommand cmd) {
        Objects.requireNonNull(cmd, "cmd must not be null.");
        Objects.requireNonNull(cmd.userId(), "userId must not be null.");

        return userStatusRepository.findByUserId(cmd.userId())
                .map(UserStatusService::toResponse)
                .orElseGet(() -> toResponse(userStatusRepository.save(new UserStatus(cmd.userId()))));
    }


    public UserStatusResponse find(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return toResponse(userStatusRepository.getOrThrow(id));
    }

    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusService::toResponse)
                .toList();
    }

    public UserStatusResponse update(UserStatusUpdateCommand cmd) {
        Objects.requireNonNull(cmd, "cmd must not be null");
        Objects.requireNonNull(cmd.id(), "id must not be null");

        UserStatus us = userStatusRepository.getOrThrow(cmd.id());
        apply(us, cmd.status(), cmd.login(), cmd.logout(), cmd.heartbeat(), cmd.unfix());
        return toResponse(userStatusRepository.save(us));
    }

    public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdCommand cmd) {
        Objects.requireNonNull(cmd, "cmd must not be null");
        Objects.requireNonNull(cmd.userId(), "userId must not be null");

        UserStatus us = userStatusRepository.getOrThrowByUserId(cmd.userId());
        apply(us, cmd.status(), cmd.login(), cmd.logout(), cmd.heartbeat(), cmd.unfix());
        return toResponse(userStatusRepository.save(us));
    }

    public boolean delete(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return userStatusRepository.deleteById(id);
    }
}
