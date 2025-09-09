package com.sprint.mission.discodeit.domain.entity;

import java.time.Instant;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@Entity
public class UserStatus extends BaseUpdatableEntity {

	@Column(nullable = false)
	private Instant lastActiveAt; // 마지막 활동 시간

	// Foreign key
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 유저 ID

	public UserStatus(
	  @NonNull User user
	) {
		this.createdAt = Instant.now();
		this.lastActiveAt = Instant.now();
		this.user = user;
	}

	public boolean isOnline() {
		return updatedAt != null &&
		  Instant.now().minusSeconds(5 * 60).isBefore(lastActiveAt);
	}

	public void setLastActiveAt(Instant lastActiveAt) {
		this.lastActiveAt = lastActiveAt;
		this.updatedAt = Instant.now();
	}
}
