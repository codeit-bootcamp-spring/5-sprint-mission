package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    JCFUserRepository repo;


    public JCFUserService(){
        this.repo=new JCFUserRepository();
    }


    public User createUser(String nick,String email, String pass){
        User user=new User(nick,email,pass);
        repo.save(user);
        System.out.println("user 추가 성공");
        return user;
    }

//    @Override
//    public UUID addMessage(UUID userId,UUID messageId) {
//        if(data.size()==0){
//            return null;
//        }
//        Message target=null;
//        for(User user:data){
//            if(user.getId().equals(userId)){
//                user.updateMessageId(messageId);
//                System.out.println("user에 message 추가 성공");
//                return messageId;
//            }
//        }
//
//
//        return null;
//    }
//
//
//    @Override
//    public UUID addChannel(UUID userId,UUID channelId) {
//        if(data.size()==0){
//            System.out.println("?");
//            return null;
//        }
//        Channel target=null;
//        for(User user:data){
//            System.out.println(user);
//            if(user.getId().equals(userId)){
//                user.updateChannelId(channelId);
//                System.out.println("user에 channel 추가 성공");
//                return channelId;
//            }
//        }
//        System.out.println("??");
//        return null;
//    }


    public User getUserById(UUID userId){
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

    ;

    public List<User> getAllUsers(){
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


    ;

    public void updateUser(UUID userId, User user){
        if(repo.count()==0){
            return ;
        }
        for(User user1: repo.findAll()){
            if(user1.getId().equals(userId)){
                user1.updateUpdatedAt(user.getUpdatedAt());
                user1.updateCreatedAt(user.getCreatedAt());
            }
        }
    };

    public void updateUserUpdatedAt(UUID userId, long updatedAt){
        if(repo.count()==0){
            return ;
        }
        for(User user1: repo.findAll()){
            if(user1.getId().equals(userId)){
                user1.updateUpdatedAt(updatedAt);
            }
        }
    };



    public User deleteUser(UUID userId){
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
//        if(repo.count()==0){
//            return null;
//        }
////        for(int i=data.size()-1;i>=0;i--){
////            if(data.get(i).getId().equals(userId)){
////                data.remove(i);
////            }
////        }
//        User target=null;
//        for(int i =0;i<repo.count();i++){
//            if(data.get(i).getId().equals(userId)){
//                target=data.get(i);
//                break;
//            }
//        }
//
//        data.removeIf(user1 -> user1.getId().equals(userId));
//        System.out.println("삭제 성공");
//        return target;

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
