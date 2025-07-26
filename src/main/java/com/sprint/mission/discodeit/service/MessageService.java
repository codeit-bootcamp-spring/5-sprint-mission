package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message register(Message message);      //메시지 등록
    Message findById(UUID id);              //메시지 아이디로 검색
    List<Message> findAll();                //모든 메시지 조회
    Message update(UUID id, String newContent);        //메시지 내용 수정
    Message delete(UUID id);         //메시지 삭제
}
