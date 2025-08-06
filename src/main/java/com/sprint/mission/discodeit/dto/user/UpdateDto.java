package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record UpdateDto(UUID id,
                        @Nullable String name,
                        @Nullable String email,
                        @Nullable BinaryContent profileImage) {
}
