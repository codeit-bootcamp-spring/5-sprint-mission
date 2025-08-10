package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.AuthLoginCommand;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private static UserResponse toResponse(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getGlobalName()
        );
    }

    protected void update(UUID id, Consumer<User> updater) {
        User entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    public UserResponse login(AuthLoginCommand cmd) {
        Objects.requireNonNull(cmd, "cmd must not be null");
        String e = Validators.validateEmail(cmd.email());
        String p = Validators.validatePassword(cmd.password());

        User user = userRepository.findByEmail(e)
                .filter(u -> u.checkPassword(p))
                .orElseThrow(() -> new NoSuchElementException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (user.isBanned()) throw new IllegalArgumentException("정지된 계정입니다.");

        update(user.getId(), User::activate);

        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseGet(() -> userStatusRepository.save(new UserStatus(user.getId())));
        status.login();
        userStatusRepository.save(status);

        return toResponse(user, status);
    }

    public boolean logout(UUID userId) {
        UserStatus userStatus = userStatusRepository.getOrThrowByUserId(userId);
        userStatus.logout();
        userStatusRepository.save(userStatus);
        return true;
    }
}
