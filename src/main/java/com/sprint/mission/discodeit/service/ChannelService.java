package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;


public interface ChannelService {
    Channel create(String channelName, String description);

    Channel find(UUID channelId);

    List<Channel> findAll();

    Channel update(UUID channelId, String channelName, String description);

    void delete(UUID channelId);

    /**
     * 모든 채널 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
