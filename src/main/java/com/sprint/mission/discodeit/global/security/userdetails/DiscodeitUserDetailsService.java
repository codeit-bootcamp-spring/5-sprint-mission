package com.sprint.mission.discodeit.global.security.userdetails;

import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserDetailsMapper userDetailsMapper;

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        User user = findUser(identifier);
        UserDetailsDto userDetailsDto = userDetailsMapper.toDto(user);
        return new DiscodeitUserDetails(userDetailsDto, user.getPassword());
    }

    private User findUser(String identifier) {
        return tryFindById(identifier)
            .orElseGet(() -> findByUsername(identifier));
    }

    private Optional<User> tryFindById(String identifier) {
        try {
            return userRepository.findById(UUID.fromString(identifier));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username));
    }
}
