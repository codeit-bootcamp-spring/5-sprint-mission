package com.sprint.mission.discodeit.service.auth;


import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto.response.UserLoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("authService")
@RequiredArgsConstructor
public class AuthService{
    private final UserRepository userRepository;

    public UserLoginResponse login(UserLoginRequest userLoginRequest){
        List<User> users=userRepository.findAll();
        User findUser=null;
        for(User user:users){
            if(user.getUsername().equals(userLoginRequest.username()) && user.getPassword().equals(userLoginRequest.password())){
                findUser=user;
                break;
            }else {
                throw new RuntimeException("일치하는 유저가 없습니다.");
            }
        }
        return new UserLoginResponse(findUser.getId(),findUser.getUsername(),findUser.getEmail());
    }

}
