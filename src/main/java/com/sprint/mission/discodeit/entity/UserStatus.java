package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_statuses")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false, updatable = false)
	private UUID userId;

    @Column(nullable = false)
	private Instant lastActiveAt;

	public UserStatus(UUID userID){
		this.userId = userID;
	}

	public UserStatus(UserStatus original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.userId = original.userId;
		this.lastActiveAt = original.lastActiveAt;
	}

	public UserStatus copy() {
		return new UserStatus(this);
	}

	public void updateLastActiveAt(Instant lastActiveAt) {
		if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
			this.lastActiveAt = lastActiveAt;
		}

	}
}
