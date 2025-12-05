package com.sprint.mission.discodeit.domain.auth.mapper;

import com.sprint.mission.discodeit.domain.auth.dto.UserDetailsDto;
import com.sprint.mission.discodeit.domain.user.entity.User;
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
