package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;


import java.util.*;

public class JCFUserService implements UserService {
    final Map<UUID, User> data= new HashMap<>();

    @Override
    public void createUser(String username, String password) {
        User user=new User(username, password);
        data.put(user.getId(),user);
    }

    @Override
    public User readByIdUser(UUID name) {
        return data.entrySet().stream()
                .filter(entry->entry.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    @Override
    public void readAllUser() {
       data.entrySet().stream()
               .forEach(entry->
                       System.out.println(entry.getKey()+" "+entry.getValue()));

    }

    @Override
    public void updateUser(UUID name, String username, String password) {
        for(Map.Entry<UUID,User>entry : data.entrySet()){
            UUID id = entry.getKey();
            User user = entry.getValue();
            if(id.equals(name)){
                user.update(username,password);
                System.out.println("수정 성공하였습니다.");
                return;
            }
        }
        System.out.println("수정 실패하였습니다.");
    }

    @Override
    public void deleteByIdUser(UUID user) {
        for(Map.Entry<UUID, User>entry:data.entrySet()){
            UUID userID = entry.getKey();
            if(userID.equals(user)){
                data.remove(userID);
                System.out.println("삭제 성공하였습니다.");
                return;
            }
        }
        System.out.println("삭제 실패하였습니다.");
    }
}
