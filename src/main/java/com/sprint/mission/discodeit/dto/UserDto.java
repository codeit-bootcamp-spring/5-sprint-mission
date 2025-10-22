package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.log.LogUtils;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {

  public String forLog() {
    return "UserDto{" +
        "id=" + id +
        ", username=" + username +
        ", email=" + LogUtils.maskEmail(email) +
        ", profile=" + LogUtils.summarizeAttachment(profile) +
        ", online=" + online +
        "}";
  }

}
