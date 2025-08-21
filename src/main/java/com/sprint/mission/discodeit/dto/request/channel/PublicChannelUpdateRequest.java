package com.sprint.mission.discodeit.dto.request.channel;

import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_NAME_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_CHANNEL_NAME_LENGTH;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @Size(min = MIN_CHANNEL_NAME_LENGTH, max = MAX_CHANNEL_NAME_LENGTH)
    String newName,

    @Size(max = 1024)
    String newDescription
) {

  @AssertTrue(message = "newName 또는 newDescription 중 하나는 필수입니다")
  public boolean hasAny() {
    return newName != null || newDescription != null;
  }
}
