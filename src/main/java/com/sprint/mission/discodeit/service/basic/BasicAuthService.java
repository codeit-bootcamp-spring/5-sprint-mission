package com.sprint.mission.discodeit.service.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;

	@Override
    @Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
        log.info("[Service] 로그인 시도");
        log.debug("[Service] 로그인 요청 데이터: {}", request);
		User user = userRepository.findByUsername(request.getUsername())
			.orElseThrow(UserNotFoundException::new);

		if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            log.warn("[Service] 잘못된 비밀번호 입력: userId={}, username={}", user.getId(), user.getUsername());
			throw new InvalidPasswordException();
		}

        log.info("[Service] 로그인 성공: userId={}, username={}", user.getId(), user.getUsername());
		return LoginResponse.success(user);
	}
}
