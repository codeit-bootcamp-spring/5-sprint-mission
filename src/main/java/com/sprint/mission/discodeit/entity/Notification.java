package com.sprint.mission.discodeit.entity;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Notification extends BaseEntity {

	@Column(nullable = false, updatable = false)
	private UUID receiverId;

	@Column(nullable = false, updatable = false)
	private String title;

	@Column(nullable = false, updatable = false)
	private String content;
}
