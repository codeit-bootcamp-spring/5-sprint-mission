package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data = new HashMap<>();


    public Channel create(String name, String topic){
        Channel channel = new Channel(name, topic);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findById(UUID id){
        return data.get(id);
    }


    public List<Channel> findByName(String name){
        List<Channel> ch = new ArrayList<>();
        for(Channel c : data.values()){
            if(c.getName().contains(name) && c.getName() != null){
                ch.add(c);
            }
        }
        return ch;
    }


    public List<Channel> findAll(){
        return new ArrayList<Channel>(data.values());
    }


    public Channel updateName(UUID id, String name){
        Channel channel = data.get(id);
        if(channel != null){
            channel.updateName(name);
        }
        return channel;
    }

    public Channel updateTopic(UUID id, String topic){
        Channel channel = data.get(id);
        if(channel != null){
            channel.updateTopic(topic);
        }
        return channel;
    }


    public boolean deleteById(UUID id){
        if(data.containsKey(id)){
            data.remove(id);
            System.out.println("삭제완료!");
            return true;
        }
        else{
            System.out.println("삭제실패: 채널을 찾을 수 없습니다");
            return false;
        }
    }

}
