package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Channel extends BaseUpdatableEntity {

  private final ChannelType type;
  private String name;
  private String description;

  public void update(String name, String description) {
    boolean anyValueUpdated = false;
    if (name != null && !name.equals(this.name)) {
      this.name = name;
      anyValueUpdated = true;
    }
    if (description != null && !description.equals(this.description)) {
      this.description = description;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      super.setUpdatedAt(Instant.now());
    }
  }
}
