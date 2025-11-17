package com.sprint.mission.discodeit.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sprint.mission.discodeit.domain.dto.user.UserDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

	private final UserDto userDto;
	private final String password;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.getRole().name()));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userDto.getUsername();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DiscodeitUserDetails that = (DiscodeitUserDetails)o;
		return Objects.equals(userDto, that.userDto) && Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userDto, password);
	}

	public static DiscodeitUserDetails from(UserDto userDto, String password) {
		return new DiscodeitUserDetails(userDto, password);

	}

	public UUID getUserId() {
		return userDto.getId();
	}

}
