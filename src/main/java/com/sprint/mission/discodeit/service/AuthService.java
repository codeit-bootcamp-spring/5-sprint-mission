package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;

public interface AuthService {
    UserResponse updateUserRole(UserRoleUpdateRequest request);

    JwtInformation refreshToken(String refreshToken);
}
