package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("UserDetails 조회: username={}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("UserDetails 조회 실패: username={} not found", username);
          return new UsernameNotFoundException("User with username %s not found".formatted(username));
        });

    UserDto userDto = userMapper.toDto(user);
    // UserDetails 에 UserDto + 인코딩된 비밀번호 저장
    return new DiscodeitUserDetails(userDto, user.getPassword());
  }
}
