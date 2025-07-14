package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    void create(Channel channel);                                       // 생성
    Channel findById(UUID id);                                          // 단건 조회
    List<Channel> findAll();                                            // 다건 조회
    void update(UUID id, String name, String description);              // 업데이트
    boolean joinUser(UUID id, UUID userId);                             // 유저 채널에서 추가
    boolean leaveUser(UUID id, UUID userId);                            // 유저 채널에서 떠남
    boolean addMessage(UUID id, UUID messageId);                        // 메세지 참조 저장
    void delete(UUID id);                                               // 삭제
}
