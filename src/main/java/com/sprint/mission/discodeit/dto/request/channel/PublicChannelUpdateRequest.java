package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @Size(min = 1, max = 100)
    String newName,

    @Size(max = 1024)
    String newDescription
) {

  @AssertTrue(message = "newName 또는 newDescription 중 하나는 필수입니다")
  public boolean hasAny() {
    return newName != null || newDescription != null;
  }
}
