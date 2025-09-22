package com.sprint.mission.discodeit.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "channels")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel extends BaseUpdatableEntity {


  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "channel_type", nullable = false)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private ChannelType type;
  @Column(nullable = false)
  private String name;
  @Column
  private String description;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Message> messages = new ArrayList<>();

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ReadStatus> readStatuses = new ArrayList<>();

  public void update(String newName, String newDescription) {
    boolean anyValueUpdated = false;

    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      anyValueUpdated = true;
    }
  }
}
