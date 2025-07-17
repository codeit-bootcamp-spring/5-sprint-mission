package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data = new HashMap<>();

    // 생성
    public Channel create(String name, String topic){
        Channel channel = new Channel(name, topic);
        data.put(channel.getId(), channel);
        return channel;
    }

    // 아이디로 조회
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    // 이름으로 조회
    public List<Channel> findByName(String name){
        if (name == null) return Collections.emptyList();
        return data.values().stream().filter(c -> c.getName().contains(name)).toList();
    }

    // 모든 채널 조회
    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    // 채널명 업데이트
    public Channel updateName(UUID id, String name){
        Channel channel = data.get(id);
        if(channel != null){
            channel.updateName(name);
        }
        return channel;
    }

    // 채널 토픽 업데이트
    public Channel updateTopic(UUID id, String topic){
        Channel channel = data.get(id);
        if(channel != null){
            channel.updateTopic(topic);
        }
        return channel;
    }

    // 채널 삭제
    public void deleteById(UUID id){
        boolean deleted = data.remove(id) != null;
        System.out.println(deleted ? "삭제완료!" : "삭제실패: 사용자를 찾을 수 없습니다");
    }

}
