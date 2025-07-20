package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;


import java.util.*;

public class JCFChannelService implements ChannelService {
    final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void createChannel(String channelname, String description) {
        Channel channel = new Channel(channelname, description);
        data.put(channel.getId(),channel);

    }

    @Override
    public Channel readByIdChannel(UUID name) {
       return data.entrySet().stream()
               .filter(entry->entry.getKey().equals(name))
               .map(entry->entry.getValue())
               .findFirst().orElse(null);
    }

    @Override
    public void readAllChannel() {
        data.entrySet().stream()
                .forEach(entry->
                        System.out.println(entry.getKey()+" "+entry.getValue()));
    }

    @Override
    public void updateChannel(UUID channelUUID,String channelname, String description) {
        for(Map.Entry<UUID,Channel>entry:data.entrySet()){
            UUID channelID = entry.getKey();
            Channel channel = entry.getValue();
            if(channelID.equals(channelUUID)){
                channel.update(channelname,description);
                System.out.println("수정 성공하였습니다.");
                return;
            }
        }
        System.out.println("수정 실패하였습니다.");
    }

    @Override
    public void deleteByIdChannel(UUID channelUUID) {
        for(Map.Entry<UUID,Channel>entry:data.entrySet()){
            UUID channelID = entry.getKey();
            if(channelID.equals(channelUUID)){
                data.remove(channelID);
                System.out.println("삭제 성공하였습니다.");
                return;
            }
        }
        System.out.println("삭제 실패하였습니다.");
    }
}
