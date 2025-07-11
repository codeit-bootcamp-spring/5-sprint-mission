package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService {
    private final List<Channel> data;

    public JCFChannelService(){
        data=new ArrayList<>();
    }

    public void createChannel(Channel channel){
        data.add(channel);
    }

    public Channel getChannelById(UUID channelId){
        if(data.size()==0){
            return null;
        }
        for(Channel channel:data){
            if(channel.getId().equals(channelId)){
                return channel;
            }
        }
        return null;
    };

    public List<Channel> getAllChannels(){
        if(data.size()==0){
            return null;
        }
        return data;
    };

    public void updateChannel(UUID channelId, Channel channel){
        if(data.size()==0){
            return ;
        }
        for(Channel channel1:data){
            if(channel1.getId().equals(channelId)){
                channel1.updateUpdatedAt(channel.getUpdatedAt());
                channel1.updateCreatedAt(channel.getCreatedAt());
            }
        }
    };

    public void deleteChannel(UUID channelId){
        if(data.size()==0){
            return ;
        }
        for(Channel channel1:data){
            if(channel1.getId().equals(channelId)){
                data.remove(channel1);
            }
        }
    };

}
