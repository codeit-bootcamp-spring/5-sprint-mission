package com.sprint.mission.discodeit.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(length = 100, nullable = false, unique = true)
	private String email;

	@Column(length = 60, nullable = false)
	private String password;

	@Column(length = 20, nullable = false)
	private Role role;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", columnDefinition = "uuid")
	private BinaryContent profile;

	@JsonManagedReference
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private UserStatus status;

	public User(String username, String email, String password, BinaryContent profile) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.profile = profile;
		this.role = Role.USER;
	}

	public User(String username, String email, String password, BinaryContent profile, Role role) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.profile = profile;
		this.role = role;
	}

	public void attachStatus(UserStatus status) {
		this.status = status;
		status.linkToUser(this);
	}

	public void update(String username, String email, String password, BinaryContent profile, Role role) {
		if (checkUpdated(username, email, password, profile, role)) {
			super.setUpdatedAt(Instant.now());
		}
	}

	private boolean checkUpdated(String username, String email, String password,
		BinaryContent profile, Role role) {
		boolean anyValueUpdated = false;

		if (username != null && !username.equals(this.username)) {
			this.username = username;
			anyValueUpdated = true;
		}
		if (email != null && !email.equals(this.email)) {
			this.email = email;
			anyValueUpdated = true;
		}
		if (password != null && !password.equals(this.password)) {
			this.password = password;
			anyValueUpdated = true;
		}
		if (profile != null && !profile.equals(this.profile)) {
			this.profile = profile;
			anyValueUpdated = true;
		}
		if (role != null && !role.equals(this.role)) {
			this.role = role;
		}

		return anyValueUpdated;
	}

}
