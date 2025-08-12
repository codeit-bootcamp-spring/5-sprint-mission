package com.sprint.mission.discodeit.dto.request;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.*;

public record UserUpdateRequest (
        UUID id,
        String username,
        String email,
        String password,
        BinaryContent binaryContent
){

}
