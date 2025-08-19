package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  //
  private final ChannelType type;
  private String name;
  private String description;

  public Channel(ChannelType type, String name, String description) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.type = type;
    this.name = name;
    this.description = description;
  }

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
      this.updatedAt = Instant.now();
    }
  }
}
