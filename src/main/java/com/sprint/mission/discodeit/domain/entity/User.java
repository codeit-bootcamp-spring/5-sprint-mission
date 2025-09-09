package com.sprint.mission.discodeit.domain.entity;

import java.util.Objects;

import com.sprint.mission.discodeit.domain.entity.base.BaseUpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
@Table(name = "\"user\"")
public class User extends BaseUpdatableEntity {

	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;

	// Foreign key
	@OneToOne(orphanRemoval = true)
	@JoinColumn(name = "profile_id")
	private BinaryContent profileImage;

	public User(String username, String email, String password, BinaryContent profileImage) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.profileImage = profileImage;
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


