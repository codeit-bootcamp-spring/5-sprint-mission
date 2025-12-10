package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContent extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false)
  private String contentType;

  @Column(nullable = false)
  @Builder.Default
  private String status = BinaryContentStatus.PROCESSING.name();

  public void updateStatus(String newStatus) {
    if (newStatus != null && !newStatus.equals(this.status)) {
      this.status = newStatus;
    }
  }
}