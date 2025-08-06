package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.annotation.Nullable;

public record JoinDto(String username,
                      String password,
                      String email,
                      @Nullable BinaryContent profileImage) {
}