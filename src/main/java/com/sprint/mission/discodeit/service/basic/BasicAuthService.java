package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.LoginParams;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.response.UserLoginResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
	private final UserRepository userRepository;

	@Override
	public User login(LoginParams params) {
		User user = userRepository.findByUsername(params.getUsername())
		  .orElseThrow(() -> new IllegalArgumentException("User with username " + params.getUsername() + " not found"));
		System.out.println(user.toString());
		if (!user.getPassword().equals(params.getPassword())) {
			throw new IllegalArgumentException("Wrong password");
		}

		return user;
	}

	public static UserLoginResponse toUserLoginResponse(User user) {
		return UserLoginResponse.builder()
		  .id(user.getId())
		  .username(user.getUsername())
		  .email(user.getEmail())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .profileId(user.getProfileImage().getId())
		  .build();
	}
}
