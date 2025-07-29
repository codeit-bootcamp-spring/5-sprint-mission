package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;
    public JCFChannelRepository(List<Channel> data){
        this.data=data;
    }
    public JCFChannelRepository(){
        this.data=new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;

    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        for(Channel channel:data){
            if(channel.getId().equals(channelId)){
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public Channel delete(UUID channelId) {
        Channel target=new Channel();
        for(Channel channel:data){
            if(channel.getId().equals(channelId)){
                target=channel;
                data.remove(channel);
            }
        }
        return target;
    }

    @Override
    public boolean existsById(UUID channelId) {
        for(Channel channel:data){
            if(channel.getId().equals(channelId)){
                return true;
            }
        }
        return false;
    }
}
