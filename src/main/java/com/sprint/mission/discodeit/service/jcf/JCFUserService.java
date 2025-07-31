package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    //유저에 저장할거 유저이름, 유저 uuid, 유저생성 시간(=업데이트시간)
    //해시맵을 이용하여 데이터 저장
    //hashmap은 key로 값을 빠르게 찾을 수 있는 자료구조.
    private final Map<UUID, User> data = new HashMap<>();

    public User create(String nickname){
        User user = new User(nickname);
        data.put(user.getId(), user);
        return user;
    }

    public User findById(UUID id){
        //존재하지 않는 id라면?.. 어떤거 반환할까
        User user = data.get(id);
        if (user == null){
            throw new NoSuchElementException("해당 id의 유저를 찾을 수 없습니다: " + id);
        }
        return user;
    }

    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String newNickname){
        User user = data.get(id);
        if(user == null){
            throw new NoSuchElementException("유저가 없습니다.");
        }else if(newNickname.equals(user.getNickname())){
            throw new NoSuchElementException("이전 닉네임과 같습니다.");
        }else{
            user.updateNickname(newNickname);
        }
    }

    public void delete(UUID id){
        User user = data.get(id);
        if(user != null){
            data.remove(id);
        }else{
            throw new NoSuchElementException("검색된 유저가 없습니다.: " + id);
        }
    }
}
