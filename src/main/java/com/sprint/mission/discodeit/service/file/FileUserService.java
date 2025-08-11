package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



public class FileUserService implements UserService {

    private final FileUserRepository fur;
    public FileUserService() {
        this.fur = new FileUserRepository();
    }
    @Override
    public void createUser(String username, String password) {
        User user = new User(username,password);
        User createuser = fur.save(user);
        System.out.println(createuser.toString());
    }

    @Override
    public User readByIdUser(UUID name) {
        if(fur.existsById(name)) {
            Optional<User> userOpt = fur.findById(name);
            return userOpt.orElse(null);
        }else{
            System.out.println("유저UUID가 존재하지 않습니다.");
            return null;
        }
    }

    @Override
    public void readAllUser() {
        long num = fur.count();
        if(num>0) {
            System.out.println("현재 등록된 유저는 "+num+"명 입니다.");
            List<User> userList = fur.findAll();
            for (User user : userList) {
                System.out.println(user.toString());
            }
        }else{
            System.out.println("현재 등록된 유저가 없습니다.");
        }
    }

    @Override
    public void updateUser(UUID user, String username, String password) {
        if(fur.existsById(user)) {
            if(fur.update(user,username,password)) {
                System.out.println("사용자 수정을 성공하였습니다.");
            }else{
                System.out.println("사용자 수정을 실패하였습니다.");
            }
        }else{
            System.out.println("유저 UUID가 존재하지 않습니다.");
        }
    }

    @Override
    public void deleteByIdUser(UUID user) {
        if(fur.existsById(user)) {
            if(fur.delete(user)){
                System.out.println("유저 삭제 성공!");
            }else{
                System.out.println("유저 삭제 실패");
            }
        }else{
            System.out.println("유저UUID가 존재하지 않습니다.");
        }

    }
}
