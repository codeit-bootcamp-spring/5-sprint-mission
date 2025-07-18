package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.IUserService;

import java.util.*;

public class JCFUserServiceImpl implements IUserService {

    public List<User> getUserData() {
        return userData;
    }

    public Map<UUID, User> getUserMap() {
        return userMap;
    }

    final List<User>  userData = new ArrayList<>();
    final Map<UUID,User> userMap = new HashMap<>();

    @Override
    public User createUser(String username, String password, String email) {
        boolean blnError = false;
        if( username == null   || password == null   || email == null )   {blnError = true;}
        if( username.isEmpty() || password.isEmpty() || email.isEmpty() ) {blnError = true;}
        if( username.isBlank() || password.isBlank() || email.isBlank() ) {blnError = true;}
        if( blnError ) {
            System.out.println("Username or Password or Email is blank");
            throw new IllegalArgumentException();
        }
        User user = new User(username, password, email);
        userData.add(user);
        userMap.put(user.getId(), user);

        return user;
    }

    @Override
    public User findById(UUID userId) {
        User rtnUser = null;
        exitFor:
        for (User user : userData) {
            if( user.getId().equals(userId) ){
                rtnUser = user;
                break exitFor;
            }
        }
        return rtnUser;
    }

    @Override
    public List<User> findAll(String searchStr) {
        List<User> rtnUserList = new ArrayList<>();
        exitFor:
        for (User user : userData) {
            if( user.getUsername().equals(searchStr) ){
                rtnUserList.add(user);
            }
        }
        return rtnUserList;
    }

    @Override
    public List<User> update(UUID userId, String chgColumnType, String orgStr, String chgStr) {
        for(User user : userData){
            if( !user.getId().equals(userId) ) {
                continue;
            }

            if( "username".equals(chgColumnType) ){
                if( user.getUsername().equals(orgStr) ){
                    System.out.println(orgStr + "을 " + chgStr + "로 변경합니다.");
                    user.setUsername(chgStr);
                }
            }else if( "password".equals(chgColumnType) ){
                if( user.getPassword().equals(orgStr) ){
                    user.setPassword(chgStr);
                }
            }
        }
        return userData;
    }

    @Override
    public List<User> delete(UUID userId) {
        StringBuffer sbf  = new StringBuffer();
        try {
            int idx = 0;
            exitFor:
            for (User user : userData) {
                sbf.append("user.getId() : ").append(user.getId()).append(":");
                sbf.append("userId : ").append(userId).append("\r\n");
                System.out.println(sbf.toString());
                if ( user.getId().equals(userId) ) {
                    System.out.println("삭제된 index 는 " + idx + "입니다.");
                    System.out.println("삭제된 userId 는 " + userId + "입니다.");
                    userData.remove(idx);
                    break exitFor;
                }
                sbf.setLength(0);
                idx++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userData;
    }
}
