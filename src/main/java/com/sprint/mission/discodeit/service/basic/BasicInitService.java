package com.sprint.mission.discodeit.service.basic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.InitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInitService implements InitService {
	@Value("${discodeit.admin.username}")
	private String adminUsername;
	@Value("${discodeit.admin.password}")
	private String adminPassword;
	@Value("${discodeit.admin.email}")
	private String adminEmail;

	@Value("${discodeit.user.username}")
	private String userUsername;
	@Value("${discodeit.user.password}")
	private String userPassword;
	@Value("${discodeit.user.email}")
	private String userEmail;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void initAdmin() {
		if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
			log.warn("관리자 계정이 이미 존재합니다");
			return;
		}

		String encodedPassword = passwordEncoder.encode(adminPassword);
		User admin = new User(
			adminUsername,
			adminEmail,
			encodedPassword,
			null,
			Role.ADMIN
		);
		userRepository.save(admin);
		log.info("관리자 계정이 초기화되었습니다");
	}

	@Override
	public void initUser() {
		if (userRepository.existsByEmail(userEmail) || userRepository.existsByUsername(userUsername)) {
			log.warn("이미 기본 사용자가 존재합니다.");
			return;
		}

		String encodedPassword = passwordEncoder.encode(userPassword);
		User user = new User(
			userUsername,
			userEmail,
			encodedPassword,
			null,
			Role.USER
		);

		userRepository.save(user);
		log.info("기본 사용자가 초기화되었습니다.");
	}
}
