package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    void create(User user);                         // 생성
    User findById(UUID id);                         // 단건 조회
    List<User> findAll();                           // 다건 조회
    void update(UUID id, String name, int age);     // 업데이트
    boolean joinChannel(UUID id, UUID channelId);   // 채널 참가
    boolean leaveChannel(UUID id, UUID channelId);  // 채널 퇴장
    boolean addMessage(UUID id, UUID messageId);
    boolean removeMessage(UUID id, UUID messageId);
    void delete(UUID id);                           // 삭제
    boolean isUserInChannel(UUID userId, UUID channelId);
}