package com.sprint.mission.discodeit.config;



import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

// Authentication(Security에서 관리하는 인증정보) 에서 사용할 실제 사용자 정보를 정의하는 객체
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "userDto")
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;


    @Override
    public int hashCode() {
        return Objects.hash(userDto.id());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DiscodeitUserDetails that = (DiscodeitUserDetails) obj;

        return Objects.equals(this.userDto.id(), that.userDto.id());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }
}
