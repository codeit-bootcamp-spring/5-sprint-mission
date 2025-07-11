package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService {
    private final List<User> data;

    public JCFUserService(){
        data=new ArrayList<>();
    }

    public void createUser(User user){
        data.add(user);
    }

    public User getUserById(UUID userId){
        if(data.size()==0){
            return null;
        }
        for(User user:data){
            if(user.getId().equals(userId)){
                return user;
            }
        }
        return null;
    };

    public List<User> getAllUsers(){
        if(data.size()==0){
            return null;
        }
        return data;
    };

    public void updateUser(UUID userId, User user){
        if(data.size()==0){
            return ;
        }
        for(User user1:data){
            if(user1.getId().equals(userId)){
                user1.updateUpdatedAt(user.getUpdatedAt());
                user1.updateCreatedAt(user.getCreatedAt());
            }
        }
    };

    public void deleteUser(UUID userId){
        if(data.size()==0){
            return ;
        }
        for(User user1:data){
            if(user1.getId().equals(userId)){
                data.remove(user1);
            }
        }
    };



}
