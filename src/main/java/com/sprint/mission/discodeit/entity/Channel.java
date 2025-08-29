package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@AllArgsConstructor
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  private ChannelType type;
  
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
