package com.sprint.mission.discodeit.global.security.userdetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@EqualsAndHashCode(of = "userDetailsDto")
public class DiscodeitUserDetails implements UserDetails {

    private final UserDetailsDto userDetailsDto;
    private final String password;

    @JsonCreator
    public DiscodeitUserDetails(
        @JsonProperty("userDetailsDto") UserDetailsDto userDetailsDto,
        @JsonProperty("password") String password
    ) {
        this.userDetailsDto = userDetailsDto;
        this.password = password;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDetailsDto.role().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return userDetailsDto.username();
    }
}
