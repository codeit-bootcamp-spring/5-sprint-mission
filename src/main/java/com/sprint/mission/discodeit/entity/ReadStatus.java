package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.mission.discodeit.entity.common.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "read_statuses")
@Getter @Setter @SuperBuilder /*@ToString*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity implements Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false, updatable = false)
	private UUID userId;
    @Column(unique = true, nullable = false, updatable = false)
	private UUID channelId;
    @Column(nullable = false)
	private Instant lastReadAt;


	public ReadStatus(UUID userId, UUID channelId) {
		this.userId = userId;
		this.channelId = channelId;
	}

	public ReadStatus(ReadStatus original) {
        super(original.getId(), original.getCreatedAt(), original.getUpdatedAt());
		this.userId = original.userId;
		this.channelId = original.channelId;
		this.lastReadAt = original.lastReadAt;
	}

	public ReadStatus copy() {
		return new ReadStatus(this);
	}

	public void update(Instant newLastReadAt) {
		if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
			this.lastReadAt = newLastReadAt;
		}
	}

}
