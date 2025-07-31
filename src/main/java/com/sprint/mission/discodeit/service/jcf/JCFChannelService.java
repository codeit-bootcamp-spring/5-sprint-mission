package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    public Channel create(String CHname){
        //자바 스트림
        boolean exists = data.values().stream()
                .anyMatch(channel -> channel.getchName().equals(CHname));
        if (exists) {
            throw new IllegalArgumentException("동일한 채널 이름이 존재합니다.");
        }
        Channel channel = new Channel(CHname);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findById(UUID id){
        Channel channel = data.get(id);
        if(channel == null){
            throw new NoSuchElementException("해당 채널을 찾을 수 없습니다." + id);
        }
        return channel;
    }

    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String newCHname){
        Channel channel = data.get(id);
        if(channel == null){
            throw new NoSuchElementException("업데이트 하려는 채널이 없습니다.");
        }else if(newCHname.equals(channel.getchName())){
            throw new NoSuchElementException("이전 이름과 같습니다.");
        }else{
            channel.updateChName(newCHname);
        }
    }

    public void delete(UUID id){
        Channel channel = data.get(id);
        if(channel == null){
            throw new NoSuchElementException("삭제하려는 채널이 없습니다.");
        }else{
            data.remove(id);
        }
    }

}
