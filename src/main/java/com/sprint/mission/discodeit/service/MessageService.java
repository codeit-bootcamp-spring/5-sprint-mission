package com.sprint.mission.discodeit.service;

//인터페이스
//기능의 약속을 정의하며 다중 구현이 가능
//CRUD(생성,읽기,모두읽기,수정,삭제)의 기능 구현하기

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    //약속
    void create(Message message); //생성
    Message findById(UUID id); //하나만 찾기
    List<Message> findAll(); //리스트에 넣음
    void update(Message message); //수정
    void delete(Message message); //삭제
}
