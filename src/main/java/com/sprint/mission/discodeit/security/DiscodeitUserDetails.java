package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto.Detail userDetail;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDetail.getRole()));
  }

  public UUID getId() {
    return userDetail.getId(); // principal.id로 접근 가능
  }
  
  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return userDetail.getUsername();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DiscodeitUserDetails that)) {
      return false;
    }
    return Objects.equals(this.userDetail.getId(), that.userDetail.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDetail.getId());
  }
}