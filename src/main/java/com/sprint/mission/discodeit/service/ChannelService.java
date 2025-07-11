package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    void create(Channel channel);                                       // 생성
    Channel findById(UUID id);                                          // 단건 조회
    List<Channel> findAll();                                            // 다건 조회
    void update(UUID id, String name, String description, int count);   // 업데이트
    void delete(UUID id);                                               // 삭제
}
