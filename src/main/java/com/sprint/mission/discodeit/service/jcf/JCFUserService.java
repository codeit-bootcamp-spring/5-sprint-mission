package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;


import java.util.*;

public class JCFUserService implements UserService {
    private final JCFUserRepository jur;


    public JCFUserService() {
        this.jur = new JCFUserRepository();
    }

    @Override
    public void createUser(String username, String password) {
        User user=new User(username, password);
        User userResult = jur.save(user);
        System.out.println(userResult.toString());
    }

    @Override
    public User readByIdUser(UUID name) {
        return jur.findById(name).orElse(null);
    }

    @Override
    public void readAllUser() {
       List<User> userList =jur.findAll();
       long num = jur.count();
       if(num>0){
           System.out.println("현재 등록된 유저는 "+num+"명 입니다.");
           for(User user : userList){
               System.out.println(user.toString());
           }
       }else{
           System.out.println("현재 등록된 유저가 없습니다.");
       }

    }

    @Override
    public void updateUser(UUID userUUID, String username, String password) {
        if(jur.existsById(userUUID)){
            if(jur.update(userUUID,username,password)){
                System.out.println("수정 성공하였습니다.");
            }else{
                System.out.println("수정 실패하였습니다.");
            }
        }else{
            System.out.println("유저UUID가 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteByIdUser(UUID user) {
       if(jur.existsById(user)) {
           if (jur.delete(user)) {
               System.out.println("삭제 성공하였습니다.");
           } else {
               System.out.println("삭제 실패하였습니다.");
           }
       }else{
           System.out.println("유저UUID가 존재하지 않습니다.");
       }
    }
}
