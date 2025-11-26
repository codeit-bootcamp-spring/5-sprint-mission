package com.sprint.mission.discodeit.initializer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.Role;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		boolean exists = userRepository.existsByRole(Role.ADMIN);

		if (exists) {
			return;
		}

		User admin = User.builder()
		  .username("admin")
		  .email("admin@admin")
		  .password(passwordEncoder.encode("admin"))
		  .profileImage(null)
		  .role(Role.ADMIN)
		  .build();

		userRepository.save(admin);

		log.info("Admin initialized");
	}
}
