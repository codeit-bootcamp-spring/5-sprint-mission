package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService  implements UserService {
    @Qualifier("fileUserRepository")
    private final UserRepository userRepository;

    @Override
    public void createUser(String username, String password) {
        User user=new User(username, password);
        User userResult = userRepository.save(user);
        System.out.println(userResult.toString());
    }

    @Override
    public User readByIdUser(UUID name) {
        return userRepository.findById(name).orElse(null);
    }

    @Override
    public void readAllUser() {
        List<User> userList =userRepository.findAll();
        long num = userRepository.count();
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
        if(userRepository.existsById(userUUID)){
            if(userRepository.update(userUUID,username,password)){
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
        if(userRepository.existsById(user)) {
            if (userRepository.delete(user)) {
                System.out.println("삭제 성공하였습니다.");
            } else {
                System.out.println("삭제 실패하였습니다.");
            }
        }else{
            System.out.println("유저UUID가 존재하지 않습니다.");
        }
    }
}
