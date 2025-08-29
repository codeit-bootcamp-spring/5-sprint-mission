package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Message extends BaseUpdatableEntity {

  private String content;
  private final UUID channelId;
  private final UUID authorId;
  private final List<UUID> attachmentIds;

  public void update(String content) {
    boolean anyValueUpdated = false;
    if (content != null && !content.equals(this.content)) {
      this.content = content;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      super.setUpdatedAt(Instant.now());
    }
  }
}
