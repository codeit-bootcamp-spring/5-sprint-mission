package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채널 추가
    Channel create(String name, UUID ownerId);

    // 채널 조회
    Channel find(UUID channelId);

    // 채널 전체 조회
    List<Channel> findAll();

    // 채널 수정
    Channel update(UUID channelId, String name, UUID ownerId);

    // 채널 삭제
    boolean delete(UUID channelId);
}
