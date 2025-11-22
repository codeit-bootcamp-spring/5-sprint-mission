package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class InitService {

    @Value("${codeit.admin.username}")
    private String adminUsername;
    @Value("${codeit.admin.password}")
    private String adminPassword;
    @Value("${codeit.admin.email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void initAdmin() {
        if (userRepository.existsByEmail(adminEmail) || userRepository.existsByUsername(adminUsername)) {
            log.warn("이미 관리자가 존재합니다.");
            return;
        }

        String encodedPassword = passwordEncoder.encode(adminPassword);
        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("관리자가 초기화되었습니다.");
    }




}
