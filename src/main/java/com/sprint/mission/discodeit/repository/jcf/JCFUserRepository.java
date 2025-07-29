package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository(){
        data=new ArrayList<>();
    }


    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        for(User user:data){
            if(user.getId().equals(userId)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public User delete(UUID userId) {
        User target=new User();
        for(User user:data){
            if(user.getId().equals(userId)){
                target=user;
                data.remove(user);
            }
        }
        return target;
    }

    @Override
    public boolean existsById(UUID userId) {
        for(User user:data){
            if(user.getId().equals(userId)){
                return true;
            }
        }
        return false;
    }
}
