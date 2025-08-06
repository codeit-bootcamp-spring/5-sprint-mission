package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface MessageService {

    void create(Message message);

    //읽기
    Message find(UUID id);

    //모두 읽기
    ArrayList<Message> allFind();

    //업데이트
    void update(UUID id, Message message);

    //삭제
    void delete(UUID id);


}
