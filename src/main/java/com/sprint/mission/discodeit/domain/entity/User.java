package com.sprint.mission.discodeit.domain.entity;

import java.util.Objects;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseUpdatableEntity {

	@JoinColumn(nullable = false, unique = true)
	private String username;
	@JoinColumn(nullable = false, unique = true)
	private String email;
	@JoinColumn(nullable = false)
	private String password;

	// Foreign key
	@OneToOne(orphanRemoval = true)
	@JoinColumn(name = "profile_id")
	private BinaryContent profileImage;

	public User(String username, String email, String password, UUID profileId) {
		this.username = username;
		this.email = email;
		this.password = password;
		// this.profileImage = profileId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User)o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}


