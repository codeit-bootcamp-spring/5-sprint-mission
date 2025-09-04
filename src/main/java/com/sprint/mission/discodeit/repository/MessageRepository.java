package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

  void save(Message message); //객체 하나 받아서 저장

  Message findById(UUID id); //하나만 찾기

  List<Message> findAll(); //전체 조회

  void update(Message message); //수정

  void delete(UUID id); //id 값만으로 삭제

  void delete(Message message);//객체 통째로 넘겨서 삭제
}
