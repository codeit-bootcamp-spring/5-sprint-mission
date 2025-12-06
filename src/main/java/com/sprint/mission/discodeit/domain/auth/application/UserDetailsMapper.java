package com.sprint.mission.discodeit.domain.auth.application;

import com.sprint.mission.discodeit.domain.auth.presentation.dto.UserDetailsDto;
import com.sprint.mission.discodeit.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsMapper {

    public UserDetailsDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDetailsDto(
            user.getId(),
            user.getUsername(),
            user.getRole()
        );
    }
}
