package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.userService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements userService {
    private final List<User> data;

    public JCFUserService(){
        data=new ArrayList<>();
    }

    public void createUser(User user){
        data.add(user);
        System.out.println("추가 성공");
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

    public void updateUserUpdatedAt(UUID userId, long updatedAt){
        if(data.size()==0){
            return ;
        }
        for(User user1:data){
            if(user1.getId().equals(userId)){
                user1.updateUpdatedAt(updatedAt);
            }
        }
    };



    public void deleteUser(UUID userId){
        if(data.size()==0){
            return ;
        }
//        for(int i=data.size()-1;i>=0;i--){
//            if(data.get(i).getId().equals(userId)){
//                data.remove(i);
//            }
//        }

        data.removeIf(user1 -> user1.getId().equals(userId));
        System.out.println("삭제 성공");

//        List<User> toRemove = new ArrayList<>();
//        for (User user1 : data) {
//            if (user1.getId().equals(userId)) {
//                toRemove.add(user1);
//            }
//        }
//        System.out.println(toRemove);
//        data.removeAll(toRemove);
    };



}
