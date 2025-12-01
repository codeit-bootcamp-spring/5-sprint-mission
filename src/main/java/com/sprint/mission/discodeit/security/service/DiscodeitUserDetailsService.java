package com.sprint.mission.discodeit.security.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername called with username: {}", username);
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.error("User not found: {}", username);
				return new UsernameNotFoundException("user not found");
			});

		log.info("User found: {}, role: {}", user.getUsername(), user.getRole());
		log.debug("Encoded password from DB: {}", user.getPassword());

		UserDto userDto = userMapper.toDto(user);

		return new DiscodeitUserDetails(userDto, user.getPassword(),
			new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
	}

}
