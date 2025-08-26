package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel register(Channel channel);      //채널 등록
    Channel findById(UUID id);        //채널 아이디로 검색
    List<Channel> findAll();                //모든 채널 조회
    Channel update(UUID id, String newDescription);        //채널 설명 수정
    Channel delete(UUID id);                //채널 삭제
}
