package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name, String description);

    // 읽기 & 모두 읽기
    Channel readChannel(UUID id);
    List<Channel> readAllChannels();

    // 수정
    Channel updateChannelname(UUID id, String name);
    Channel updateDescription(UUID id, String description);

    // 삭제
    boolean deleteChannel(UUID id);
}
