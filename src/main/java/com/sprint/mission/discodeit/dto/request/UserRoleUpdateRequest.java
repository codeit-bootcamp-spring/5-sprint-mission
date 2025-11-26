package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UserRoleUpdateRequest(

    @NotNull(message = "사용자의 아이디는 존재해야 합니다.")
    UUID userId,

    @NotNull(message = "변경하고자 하는 권한은 존재해야 합니다.")
    Role newRole
) {

}
