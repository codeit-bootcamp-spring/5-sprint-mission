package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.channelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements channelService {
    private final List<Channel> data;

    public JCFChannelService(){
        data=new ArrayList<>();
    }

    public void createChannel(Channel channel){
        data.add(channel);
        System.out.println("추가 성공");
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

    public void updateChannelUpdatedAt(UUID channelId, long updatedAt){
        if(data.size()==0){
            return ;
        }
        for(Channel channel1:data){
            if(channel1.getId().equals(channelId)){
                channel1.updateUpdatedAt(updatedAt);
            }
        }
    };


    public void deleteChannel(UUID channelId){
        if(data.size()==0){
            return ;
        }
        data.removeIf(channel1 -> channel1.getId().equals(channelId));
        System.out.println("삭제 성공");
//        List<Channel> toRemove = new ArrayList<>();
//        for (Channel channel1 : data) {
//            if (channel1.getId().equals(channelId)) {
//                toRemove.add(channel1);
//            }
//        }
//        System.out.println(toRemove);
//        data.removeAll(toRemove);
    };

}
