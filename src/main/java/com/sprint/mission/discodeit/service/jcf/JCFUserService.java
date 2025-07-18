package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new HashMap<>();


    @Override
    public User createUser(String username, String password, String nickname) {

        if(username == null || username.isBlank() ||
           password == null || password.isBlank() ||
           nickname == null || nickname.isBlank()){
           throw new IllegalArgumentException("사용자 이름, 비밀번호, 닉네임은 필수 입력사항입니다.");
        }

        User user = new User(username, password, nickname);
        data.put(user.getId(), user);
        return user;

    }

    @Override
    public Optional<User> findUser(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(UUID userId, String username, String password, String nickname) {
        User user = data.get(userId);
        if(user==null){
            throw new IllegalArgumentException("없는 아이디입니다.");
        }
        if(username == null || username.isBlank() ||
           password == null || password.isBlank() ||
           nickname == null || nickname.isBlank()){
           throw new IllegalArgumentException("사용자 이름, 비밀번호, 닉네임은 필수 입력사항입니다.");
        }

        return user.update(username, password, nickname);
    }

    @Override
    public User deleteUser(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("삭제할 사용자 ID는 필수입니다.");
        }
        User removedUser = data.remove(userId);

        if (removedUser == null) {
            throw new NoSuchElementException(userId + "에 해당하는 사용자를 찾을 수 없어 삭제할 수 없습니다.");
        }

        return removedUser;
    }
}
