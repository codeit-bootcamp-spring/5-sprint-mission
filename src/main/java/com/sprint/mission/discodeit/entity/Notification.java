package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;
  @Column(nullable = false)
  private String type;
  @Column(nullable = false)
  private String title;
  @Column(nullable = false)
  private String content;
}
