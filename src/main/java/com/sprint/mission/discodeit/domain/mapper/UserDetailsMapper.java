package com.sprint.mission.discodeit.domain.mapper;

import com.sprint.mission.discodeit.domain.dto.auth.data.UserDetailsDto;
import com.sprint.mission.discodeit.domain.entity.User;
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
