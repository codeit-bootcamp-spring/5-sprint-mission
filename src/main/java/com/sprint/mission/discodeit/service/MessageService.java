package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    void create(Message message);           // 생성
    Message findById(UUID id);              // 단건 조회
    List<Message> findAll();                // 다건 조회
    void update(UUID id, String content);   // 업데이트
    void delete(UUID id);                   // 삭제
}
