package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("userStatusService")
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    public UserStatus create(UserStatusRequest request){
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        for(UserStatus userStatus : userStatuses){
            if(!userStatus.getId().equals(request.user().getId()) || !userRepository.existsById(request.user().getId()) ){
                throw new RuntimeException("User가 존재하지 않거나 User와 관련된 객체가 이미 존재합니다.");
            }
        }
        return userStatusRepository.save(new  UserStatus(request.user()));


    }

    public UserStatus find(UUID userId){
        return userStatusRepository.findById(userId);
    }

    public List<UserStatus> findAll(){
        return userStatusRepository.findAll();
    }

    public UserStatus update(UserStatusUpdateRequest request){
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        for(UserStatus userStatus : userStatuses){
            if(userStatus.getId().equals(request.statusId())){
                userStatus.setLogin(request.log());
                return userStatus;
            }
        }
        throw new RuntimeException("update되지 않았습니다.");
    }

    public void delete(UUID id){
        userStatusRepository.deleteById(id);
    }






}
