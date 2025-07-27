package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채널 자체의 생성, 검색 메서드
    Channel createChannel(String channelName, String channelIntroduction, int typeValue);
    Channel find(UUID id);
    List<Channel> findAll();

    // 채널 정보 수정(이름, 소개) 메서드
    Channel updateChannelName(UUID id, String channelName);
    Channel updateChannelIntroduction(UUID id, String channelIntroduction);

    // 채널 삭제 메서드
    boolean delete(UUID id);

    // 유저가 특정 채널 입장 및 퇴장하는 메서드
    Channel enter(UUID userId, UUID channelId); // 미구현
    Channel leave(UUID userId, UUID channelId); // 미구현

}
