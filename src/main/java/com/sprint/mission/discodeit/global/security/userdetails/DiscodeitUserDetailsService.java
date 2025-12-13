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
        User user = findUserByIdentifier(identifier)
            .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + identifier));
        UserDetailsDto userDetailsDto = userDetailsMapper.toDto(user);
        return new DiscodeitUserDetails(userDetailsDto, user.getPassword());
    }

    private Optional<User> findUserByIdentifier(String identifier) {
        try {
            UUID userId = UUID.fromString(identifier);
            return userRepository.findById(userId);
        } catch (IllegalArgumentException e) {
            return userRepository.findByUsername(identifier);
        }
    }
}
