package com.codeit.mission.discodeit.service;


import com.codeit.mission.discodeit.dto.request.LoginRequest;
import com.codeit.mission.discodeit.entity.User;

public interface AuthService {
    User login(LoginRequest loginRequest);
}