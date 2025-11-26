package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserResponse userResponse;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userResponse.getRole().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userResponse.getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscodeitUserDetails that = (DiscodeitUserDetails) o;
        return Objects.equals(userResponse.getId(), that.userResponse.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userResponse.getId());
    }
}