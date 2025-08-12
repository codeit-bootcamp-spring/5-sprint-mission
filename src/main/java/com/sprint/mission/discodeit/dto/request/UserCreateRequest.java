package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.*;


public record UserCreateRequest(
        UUID id,
        String username,
        String email,
        String password,
        BinaryContent binaryContent
        ) {

        public UserCreateRequest(UUID id, String username, String email, String password) {
                this(id, username, email, password, null);
        }
}
