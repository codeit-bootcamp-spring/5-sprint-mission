package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_statuses")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Column(nullable = false)
	private Instant lastActiveAt;

    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.EPOCH;
    }

	public UserStatus(UserStatus original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.user = original.user;
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
