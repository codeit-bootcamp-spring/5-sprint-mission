package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    // 생성
    Channel createChannel(String name, String description, ChannelType channelType);

    // 읽기 & 모두 읽기
    Optional<Channel> readChannel(UUID id);
    List<Channel> readAllChannels();

    // 수정
    Channel updateChannel(Channel channel);

    // 삭제
    void deleteChannel(UUID id);


}
