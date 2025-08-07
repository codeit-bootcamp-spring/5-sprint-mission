package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record ChannelCreateRequest(
        @NotBlank String name,
        String description,
        List<User> users
) {
}
