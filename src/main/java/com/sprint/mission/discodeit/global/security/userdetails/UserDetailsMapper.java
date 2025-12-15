package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.domain.User;
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
