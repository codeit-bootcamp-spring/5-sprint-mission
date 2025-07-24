package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // 생성
    public Channel create(String name, String topic){
        Channel channel = new Channel(name, topic);
        return channelRepository.save(channel);
    }

    // 아이디로 조회
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(channelRepository.findById(id));
    }

    // 이름으로 조회
    public List<Channel> findByName(String name){
        return channelRepository.findByName(name);
    }

    // 모든 채널 조회
    public List<Channel> findAll(){
        return channelRepository.findAll();
    }

    // 채널명 업데이트
    public Channel updateName(UUID id, String name){
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 ID의 채널을 찾을 수 없습니다");
        }
        channel.updateName(name);
        return channelRepository.save(channel);
    }

    // 채널 토픽 업데이트
    public Channel updateTopic(UUID id, String topic){
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 ID의 토픽을 찾을 수 없습니다");
        }
        channel.updateTopic(topic);
        return channelRepository.save(channel);
    }

    // 채널 삭제
    public void deleteById(UUID id){
        channelRepository.deleteById(id);
    }

}
