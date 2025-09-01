package com.sprint.mission.discodeit.dto.user.data;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userstatus.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.entity.sub.BinaryContent;
import com.sprint.mission.discodeit.entity.sub.UserStatus;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online
) {}
