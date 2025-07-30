package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    FileUserRepository repo;
    public FileUserService(){
        this.repo=new FileUserRepository();
    }

    @Override
    public User createUser(String nick, String email, String pass) {
        User user=new User(nick,email,pass);
        repo.save(user);
        System.out.println("user 추가 성공");
        return user;
    }

    @Override
    public User getUserById(UUID userId) {

        if(repo.count()==0){
            return null;
        }
        for(User user:repo.findAll()){
            if(user.getId().equals(userId)){
                return user;
            }
        }
        return null;
    }

    @Override
    public UUID getUserIdByNick(String nick) {

        if(repo.count()==0){
            return null;
        }
        for(User user:repo.findAll()){
            if(user.getId().equals(nick)){
                return user.getId();
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {

        if(repo.count()==0){
            return null;
        }
        return repo.findAll();
    }

    @Override
    public User updateUserNick(UUID userId, String nick) {

        if(repo.count()==0){
            return null;
        }
        User target=null;
        for(User user: repo.findAll()){
            if(user.getId().equals(userId)){
                if(user.getNick().equals(nick)){
                    return null;
                }
                user.updateNick(nick);
                target=user;
            }
        }

        return target;
    }

    @Override
    public User updateUserPass(UUID userId, String pass) {

        if(repo.count()==0){
            return null;
        }
        User target=null;
        for(User user: repo.findAll()){
            if(user.getId().equals(userId)){
                if(user.getPass().equals(pass)){
                    return null;
                }
                user.updatePass(pass);
                target=user;
            }
        }

        return target;
    }

    @Override
    public User updateUserEmail(UUID userId, String email) {

        if(repo.count()==0){
            return null;
        }
        User target=null;
        for(User user: repo.findAll()){
            if(user.getId().equals(userId)){
                if(user.getEmail().equals(email)){
                    return null;
                }
                user.updateEmail(email);
                target=user;
            }
        }

        return target;
    }

    @Override
    public User deleteUser(UUID userId) {

        User target=null;
        if(repo.count()==0){
            return null;
        }
        for(User user:repo.findAll()){
            if(user.getId().equals(userId)){
                target=user;
                repo.delete(userId);
            }
        }
        return target;
    }
}
