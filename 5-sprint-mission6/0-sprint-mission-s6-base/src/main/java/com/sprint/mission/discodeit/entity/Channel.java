package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 10)
  private ChannelType type;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  public Channel(ChannelType type, String name, String description) {
    if (type == null) throw new IllegalArgumentException("type is required");
    if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public boolean update(String newName, String newDescription) {
    boolean changed = false;
    if (newName != null && !newName.equals(this.name)) { this.name = newName; changed = true; }
    if (newDescription != null && !newDescription.equals(this.description)) { this.description = newDescription; changed = true; }
    return changed;
  }
}
