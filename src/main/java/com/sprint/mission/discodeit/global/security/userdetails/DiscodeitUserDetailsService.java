package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.domain.auth.application.UserDetailsMapper;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.UserDetailsDto;
import com.sprint.mission.discodeit.domain.user.domain.User;
import com.sprint.mission.discodeit.domain.user.domain.UserRepository;
import com.sprint.mission.discodeit.global.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserDetailsMapper userDetailsMapper;

    @Override
    @Cacheable(value = CacheName.USER_DETAILS, key = "#username")
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자명입니다: " + username));
        UserDetailsDto userDetailsDto = userDetailsMapper.toDto(user);
        return new DiscodeitUserDetails(userDetailsDto, user.getPassword());
    }
}
