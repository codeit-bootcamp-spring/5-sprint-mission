package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private static final Map<UUID,User> data = new HashMap<>();

    private final ChannelService channelService;
    public JCFUserService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void add(User user) {
        if(user == null){
            throw new IllegalArgumentException("user는 null일 수 없다.");
        }
        data.put(user.getId(), user);
    }

    @Override
    public User findOne(UUID userId) {
        if(userId == null){
            throw new IllegalArgumentException("userId는 null일 수 없다.");
        }
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID originUserUuid , User newUser) {
        if(originUserUuid == null || newUser == null){
            throw new IllegalArgumentException("userId 혹은 user는 null일 수 없다.");
        }

        User existingUser = data.remove(originUserUuid);

        existingUser.updateUserName(newUser.getUserName());
        existingUser.updateEmail(newUser.getEmail());
        existingUser.updatePassword(newUser.getPassword());
        existingUser.updatePhoneNumber(newUser.getPhoneNumber());

        data.put(existingUser.getId(), existingUser);
    }
    @Override
    public void delete(UUID userId) {
        if(userId == null){
            throw new IllegalArgumentException("userId는 null일 수 없다.");
        }

        data.remove(userId);
    }

    @Override
    public void deleteAll(){
        data.clear();
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        if(channelId == null || userId == null){
            throw new IllegalArgumentException("channelId와 userId는 null일 수 없다.");
        }

        Channel channel = channelService.findOne(channelId);
        User user = findOne(userId);
        channel.addUser(user);
    }
}
