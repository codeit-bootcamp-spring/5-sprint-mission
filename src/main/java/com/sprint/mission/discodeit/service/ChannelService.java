package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    void create(Channel channel);                                       // 생성
    Channel findById(UUID id);                                          // 단건 조회
    List<Channel> findAll();                                            // 다건 조회
    void update(UUID id, String name, String description);              // 업데이트
    boolean addUser(UUID id, UUID userId);                              // 유저 채널에서 추가
    boolean removeUser(UUID id, UUID userId);                           // 유저 채널에서 제거
    boolean addMessage(UUID id, UUID messageId);
    boolean removeMessage(UUID id, UUID messageId);
    void delete(UUID id);                                               // 삭제
}
