package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.domain.auth.presentation.dto.UserDetailsDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "userDetailsDto")
public class DiscodeitUserDetails implements UserDetails {

    private final UserDetailsDto userDetailsDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDetailsDto.role().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userDetailsDto.username();
    }
}
