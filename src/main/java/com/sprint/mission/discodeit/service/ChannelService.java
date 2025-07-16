package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelService {
    void register(Channel channel);      //채널 등록
    Channel findByName(String name);        //채널 이름으로 검색
    List<Channel> findAll();                //모든 채널 조회
    boolean update(Channel channel);        //채널 수정조회
    boolean deleteChannel();                //채널 삭제
}
